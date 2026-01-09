package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Active reference entity. Part of database schema 'references_schema'.
 */
@Entity
@Data
@Table(name = "active_reference", schema = "references_schema")
public class ActiveReferenceEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(cascade = CascadeType.ALL)
  private DocumentReferenceEntity source;

  @ManyToOne(cascade = CascadeType.ALL)
  private DocumentReferenceEntity target;
}
