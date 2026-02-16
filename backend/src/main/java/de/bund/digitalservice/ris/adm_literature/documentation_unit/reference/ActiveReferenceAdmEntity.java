package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Active reference adm entity for storing active references from SLI/ULI to ADM.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "active_reference_adm", schema = "literature")
public class ActiveReferenceAdmEntity extends AbstractActiveReferenceSliAdm {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(optional = false)
  private DocumentationUnitEntity sourceDocumentationUnit;
}
