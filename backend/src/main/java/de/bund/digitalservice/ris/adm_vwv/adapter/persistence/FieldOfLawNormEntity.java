package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "field_of_law_norm_view")
public class FieldOfLawNormEntity {
  @Id private UUID id;

  private String abbreviation;

//  @Column(name = "single_norm_description")
  private String singleNormDescription;

  @ManyToOne
  @JoinColumn(name = "field_of_law_id")
  private FieldOfLawEntity fieldOfLaw;
}
