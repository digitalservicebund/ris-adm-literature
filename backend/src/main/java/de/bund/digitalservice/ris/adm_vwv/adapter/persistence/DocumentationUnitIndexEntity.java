package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Documentation unit index JPA entity.
 */
@Entity
@Data
@EqualsAndHashCode(exclude = "documentationUnit")
@ToString(exclude = "documentationUnit")
@Table(name = "documentation_unit_index")
public class DocumentationUnitIndexEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @OneToOne(optional = false)
  private DocumentationUnitEntity documentationUnit;

  @Basic
  private String langueberschrift;

  @Basic
  private String fundstellen;

  @Basic
  private String zitierdaten;
}
