package de.bund.digitalservice.ris.adm_literature.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.Immutable;

/**
 * Field of law norm JPA entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-literature}.
 * </p>
 */
@Data
@Entity
@Table(name = "field_of_law_norm_view")
@Immutable
public class FieldOfLawNormEntity {

  @Id
  private UUID id;

  private String abbreviation;

  private String singleNormDescription;

  @ManyToOne
  @JoinColumn(name = "field_of_law_id")
  private FieldOfLawEntity fieldOfLaw;
}
