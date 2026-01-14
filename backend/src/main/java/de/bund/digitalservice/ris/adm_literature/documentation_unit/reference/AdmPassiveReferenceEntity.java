package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Adm passive reference view entity. Part of database schema 'references_schema'.
 */
@Entity
@Data
@Table(name = "adm_passive_reference_view", schema = "references_schema")
public class AdmPassiveReferenceEntity {

  @Id
  private UUID activeReferenceId;

  @Enumerated(EnumType.STRING)
  @Basic(optional = false)
  private DocumentCategory sourceDocumentCategory;

  @Basic(optional = false)
  private String sourceDocumentNumber;

  /**
   * The target document number is always an adm document number.
   */
  @Basic(optional = false)
  private String targetDocumentNumber;
}
