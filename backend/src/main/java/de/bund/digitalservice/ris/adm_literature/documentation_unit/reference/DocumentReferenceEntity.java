package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Document reference entity. Part of database schema 'references_schema'.
 */
@Entity
@Data
@Table(name = "document_reference", schema = "references_schema")
public class DocumentReferenceEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Basic
  private String admDocumentNumber;

  @Basic
  private String literatureDocumentNumber;

  @Enumerated(EnumType.STRING)
  private DocumentCategory documentCategory;

  /**
   * Sets the field of this class correctly by the given document category.
   *
   * @param documentReference The document reference
   */
  public void setDocumentReference(DocumentReference documentReference) {
    documentCategory = documentReference.documentCategory();
    switch (documentCategory) {
      case VERWALTUNGSVORSCHRIFTEN -> admDocumentNumber = documentReference.documentNumber();
      case LITERATUR_SELBSTAENDIG, LITERATUR_UNSELBSTAENDIG -> literatureDocumentNumber =
        documentReference.documentNumber();
      default -> throw new IllegalArgumentException(
        "Invalid document category: " + documentCategory
      );
    }
  }
}
