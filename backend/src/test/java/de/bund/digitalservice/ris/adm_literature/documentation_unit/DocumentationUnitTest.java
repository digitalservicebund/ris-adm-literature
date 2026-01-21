package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentationUnitTest {

  private static Stream<Arguments> documentationUnits() {
    return Stream.of(
      Arguments.of("{}", "<xml/>", true),
      Arguments.of(null, "<xml/>", true),
      Arguments.of("{}", null, false),
      Arguments.of(null, null, false)
    );
  }

  @ParameterizedTest
  @MethodSource("documentationUnits")
  void isPublished(String json, String xml, boolean expected) {
    // given
    DocumentationUnit documentationUnit = new DocumentationUnit(
      "documentNumber",
      UUID.randomUUID(),
      json,
      xml
    );

    // when
    boolean actualPublished = documentationUnit.isPublished();

    // then
    assertThat(actualPublished).isEqualTo(expected);
  }
}
