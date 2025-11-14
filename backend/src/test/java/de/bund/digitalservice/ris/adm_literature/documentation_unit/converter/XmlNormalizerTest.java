package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class XmlNormalizerTest {

  private static Stream<Arguments> texts() {
    return Stream.of(
      // text, expected
      Arguments.of(
        """
        <akn:proprietary source="attributsemantik-noch-undefiniert">
          <ris:meta>
          </ris:meta>
        </akn:proprietary>""",
        """
        <akn:proprietary source="attributsemantik-noch-undefiniert"><ris:meta></ris:meta></akn:proprietary>"""
      ),
      Arguments.of(
        """
        <p>
          Erste Zeile

            Zweite Zeile eingerückt
        </p>
        """,
        """
        <p>
        Erste Zeile

        Zweite Zeile eingerückt
        </p>"""
      ),
      Arguments.of("      <a>      <b>    <c>", "<a><b><c>"),
      Arguments.of(
        "<a>Zeile 1\r\n Zeile 2\n<b>Zeile 3\r\n Zeile 4\r\n<c>Zeile 5",
        """
        <a>Zeile 1
        Zeile 2
        <b>Zeile 3
        Zeile 4
        <c>Zeile 5"""
      )
    );
  }

  @ParameterizedTest
  @MethodSource("texts")
  void applyNormalizeFunction(String text, String expected) {
    // given

    // when
    String actual = text.transform(XmlNormalizer.NORMALIZE_FUNCTION);

    // then
    assertThat(actual).isEqualTo(expected);
  }
}
