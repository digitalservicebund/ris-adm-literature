package de.bund.digitalservice.ris.adm_vwv.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentationUnitTest {

  private static Stream<Arguments> documentationUnits() {
    return Stream.of(
      Arguments.of("{}", "<xml/>", false),
      Arguments.of(null, "<xml/>", false),
      Arguments.of("{}", null, false),
      Arguments.of(null, null, true)
    );
  }

  @ParameterizedTest
  @MethodSource("documentationUnits")
  void isEmpty(String json, String xml, boolean expected) {
    // given
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "KSNR",
      UUID.randomUUID(),
      json,
      xml
    );

    // when
    boolean actualEmpty = documentationUnit.isEmpty();

    // then
    assertThat(actualEmpty).isEqualTo(expected);
  }
}
