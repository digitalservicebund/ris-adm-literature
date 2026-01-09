package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchIllegalArgumentException;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import org.junit.jupiter.api.Test;

class DocumentReferenceEntityTest {

  @Test
  void setDocumentReference_adm() {
    // given
    DocumentReferenceEntity documentReferenceEntity = new DocumentReferenceEntity();

    // when
    documentReferenceEntity.setDocumentReference(
      new DocumentReference("KSNR2026000001", DocumentCategory.VERWALTUNGSVORSCHRIFTEN)
    );

    // then
    assertThat(documentReferenceEntity)
      .extracting(
        DocumentReferenceEntity::getAdmDocumentNumber,
        DocumentReferenceEntity::getDocumentCategory
      )
      .containsExactly("KSNR2026000001", DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
  }

  @Test
  void setDocumentReference_literature() {
    // given
    DocumentReferenceEntity documentReferenceEntity = new DocumentReferenceEntity();

    // when
    documentReferenceEntity.setDocumentReference(
      new DocumentReference("KSLU2026000002", DocumentCategory.LITERATUR_UNSELBSTAENDIG)
    );

    // then
    assertThat(documentReferenceEntity)
      .extracting(
        DocumentReferenceEntity::getLiteratureDocumentNumber,
        DocumentReferenceEntity::getDocumentCategory
      )
      .containsExactly("KSLU2026000002", DocumentCategory.LITERATUR_UNSELBSTAENDIG);
  }

  @Test
  void setDocumentReference_unvalid() {
    // given
    DocumentReferenceEntity documentReferenceEntity = new DocumentReferenceEntity();

    // when
    IllegalArgumentException exception = catchIllegalArgumentException(() ->
      documentReferenceEntity.setDocumentReference(
        new DocumentReference("document_number", DocumentCategory.LITERATUR)
      )
    );

    // then
    assertThat(exception).message().isEqualTo("Invalid document category: LITERATUR");
  }
}
