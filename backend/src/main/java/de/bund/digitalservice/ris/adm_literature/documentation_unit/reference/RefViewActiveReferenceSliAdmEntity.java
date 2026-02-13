package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.jpa.AuditingMetadata;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Ref view entity for active reference SLI to ADM.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = "ref_view_active_reference_sli_adm", schema = "references_schema")
@Immutable
public class RefViewActiveReferenceSliAdmEntity {

  @Id
  private UUID id;

  @Basic
  private UUID sourceDocumentationUnitId;

  @Basic
  private String sourceDocumentNumber;

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
