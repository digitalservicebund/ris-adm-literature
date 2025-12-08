package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.TestAdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.TestLiteratureUnitContent;
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
        TestLiteratureUnitContent.createUli("", ""),
        DocumentCategory.LITERATUR_UNSELBSTAENDIG
      ),
      Arguments.of(
        TestLiteratureUnitContent.createSli("", ""),
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
