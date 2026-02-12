package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import de.bund.digitalservice.ris.adm_literature.jpa.AuditingMetadata;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Active reference adm entity for storing active references from SLI/ULI to ADM.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = "active_reference_adm", schema = "literature")
public class ActiveReferenceAdmEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(optional = false)
  private DocumentationUnitEntity sourceDocumentationUnit;

  @Basic
  private UUID targetDocumentationUnitId;

  @Basic
  private String targetDocumentNumber;

  @Basic(optional = false)
  private String zitierart;

  @Basic
  private String normgeber;

  @Basic
  private String inkrafttretedatum;

  @Basic
  private String aktenzeichen;

  @Basic(optional = false)
  private String fundstelle;

  @Basic
  private String dokumenttyp;

  @Embedded
  private AuditingMetadata auditingMetadata = new AuditingMetadata();
}
