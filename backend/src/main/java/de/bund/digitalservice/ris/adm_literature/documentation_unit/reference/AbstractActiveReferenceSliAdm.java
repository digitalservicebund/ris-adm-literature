package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.jpa.AuditingMetadata;
import jakarta.persistence.Basic;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.Data;

/**
 * Abstract active reference superclass for SLI to ADM.
 */
@MappedSuperclass
@Data
public abstract class AbstractActiveReferenceSliAdm {

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
