package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentationUnitContentTest {

  private static Stream<Arguments> documentationUnitContentInstances() {
    return Stream.of(
      Arguments.of(
        TestAdmDocumentationUnitContent.create("", ""),
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN
      ),
      Arguments.of(
        TestDocumentationUnitContent.createUli("", ""),
        DocumentCategory.LITERATUR_UNSELBSTAENDIG
      ),
      Arguments.of(
        TestDocumentationUnitContent.createSli("", ""),
        DocumentCategory.LITERATUR_SELBSTAENDIG
      )
    );
  }

  @ParameterizedTest
  @MethodSource("documentationUnitContentInstances")
  void documentCategory(
    DocumentationUnitContent documentationUnitContent,
    DocumentCategory expected
  ) {
    // given

    // when
    DocumentCategory documentCategory = documentationUnitContent.documentCategory();

    // then
    assertThat(documentCategory).isEqualTo(expected);
  }
}
