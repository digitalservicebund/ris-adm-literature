package de.bund.digitalservice.ris.adm_literature.documentation_unit.references;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

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

  public void setReference(ReferenceItem referenceItem) {
    documentCategory = referenceItem.documentCategory();
    switch (documentCategory) {
      case VERWALTUNGSVORSCHRIFTEN -> admDocumentNumber = referenceItem.documentNumber();
      case LITERATUR_SELBSTAENDIG, LITERATUR_UNSELBSTAENDIG -> literatureDocumentNumber =
        referenceItem.documentNumber();
      default -> throw new IllegalArgumentException(
        "Invalid document category: " + documentCategory
      );
    }
  }
}
