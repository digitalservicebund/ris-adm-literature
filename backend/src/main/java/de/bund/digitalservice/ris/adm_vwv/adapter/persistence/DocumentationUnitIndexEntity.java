package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Documentation unit index JPA entity.
 */
@Entity
@Data
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
