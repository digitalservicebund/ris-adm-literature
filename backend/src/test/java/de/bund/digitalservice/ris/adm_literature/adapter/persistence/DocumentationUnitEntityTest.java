package de.bund.digitalservice.ris.adm_literature.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentationUnitEntityTest {

  private static Stream<Arguments> documentationUnitEntities() {
    return Stream.of(
      Arguments.of("{}", "<xml/>", false),
      Arguments.of(null, "<xml/>", false),
      Arguments.of("{}", null, false),
      Arguments.of(null, null, true)
    );
  }

  @ParameterizedTest
  @MethodSource("documentationUnitEntities")
  void isEmpty(String json, String xml, boolean expected) {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setJson(json);
    documentationUnitEntity.setXml(xml);

    // when
    boolean actualEmpty = documentationUnitEntity.isEmpty();

    // then
    assertThat(actualEmpty).isEqualTo(expected);
  }
}
