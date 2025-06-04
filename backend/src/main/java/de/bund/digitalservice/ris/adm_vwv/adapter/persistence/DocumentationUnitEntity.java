package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Documentation unit JPA entity.
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

  @Basic
  private String xml;

  @OneToOne(mappedBy = "documentationUnit")
  private DocumentationUnitIndexEntity documentationUnitIndex;

  /**
   * Returns {@code true} if this documentation unit has no content, json and xml are set to {@code null}.
   *
   * @return {@code true} if this documentation unit is empty, {@code false} otherwise
   */
  public boolean isEmpty() {
    return json == null && xml == null;
  }
}
