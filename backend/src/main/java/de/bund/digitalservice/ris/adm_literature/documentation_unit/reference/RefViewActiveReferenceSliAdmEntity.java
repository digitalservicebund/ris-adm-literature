package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Ref view entity for active reference SLI to ADM.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "ref_view_active_reference_sli_adm", schema = "references_schema")
@Immutable
public class RefViewActiveReferenceSliAdmEntity extends AbstractActiveReferenceSliAdm {

  @Id
  private UUID id;

  @Basic
  private UUID sourceDocumentationUnitId;

  @Basic
  private String sourceDocumentNumber;
}
