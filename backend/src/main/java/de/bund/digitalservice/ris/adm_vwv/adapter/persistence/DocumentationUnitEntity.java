package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Database entity for documentation units
 */
@Entity
@Data
@Table(name = "documentation_unit")
public class DocumentationUnitEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Basic(optional = false)
  private String documentNumber;

  @Basic
  private String json;
}
