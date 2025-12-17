package de.bund.digitalservice.ris.adm_literature.documentation_unit.notes;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Note JPA entity.
 */
@Entity
@Data
@EqualsAndHashCode(exclude = "documentationUnit")
@ToString(exclude = "documentationUnit")
@Table(name = "documentation_unit_note")
public class NoteEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @OneToOne(optional = false)
  private DocumentationUnitEntity documentationUnit;

  @Basic(optional = false)
  private String note;
}
