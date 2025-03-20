package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "field_of_law_view")
public class FieldOfLawEntity {

  @Id
  private UUID id;

  private String identifier;

  private String text;

  @OneToMany(mappedBy = "fieldOfLaw", fetch = FetchType.LAZY)
  private Set<FieldOfLawNormEntity> norms = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinTable(
    name = "field_of_law_field_of_law_parent_view",
    joinColumns = @JoinColumn(name = "field_of_law_id"),
    inverseJoinColumns = @JoinColumn(name = "field_of_law_parent_id")
  )
  private FieldOfLawEntity parent;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
  @OrderBy("identifier")
  @Builder.Default
  private Set<FieldOfLawEntity> children = new HashSet<>();

  private Integer jurisId;

  private String notation;
}
