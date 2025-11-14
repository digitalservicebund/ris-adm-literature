package de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;
import org.springframework.data.annotation.Immutable;

/**
 * Field of law JPA entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-literature}.
 * </p>
 */
@Data
@Entity
@EqualsAndHashCode(exclude = { "children", "parent", "norms", "fieldOfLawTextReferences" })
@ToString(exclude = "children")
@Table(name = "field_of_law_view")
@Immutable
public class FieldOfLawEntity {

  @Id
  private UUID id;

  private String identifier;

  private String text;

  @OneToMany(mappedBy = "fieldOfLaw", fetch = FetchType.LAZY)
  private Set<FieldOfLawNormEntity> norms = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "field_of_law_parent_id")
  private FieldOfLawEntity parent;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
  @OrderBy("identifier")
  private Set<FieldOfLawEntity> children = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "field_of_law_field_of_law_text_reference_view",
    joinColumns = @JoinColumn(name = "field_of_law_id"),
    inverseJoinColumns = @JoinColumn(name = "field_of_law_text_reference_id")
  )
  private Set<FieldOfLawEntity> fieldOfLawTextReferences = new HashSet<>();

  private Integer jurisId;

  private String notation;
}
