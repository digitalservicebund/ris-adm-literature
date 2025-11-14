package de.bund.digitalservice.ris.adm_literature.lookup_tables.institution;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.RegionEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Institution JPA entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-literature}.
 * </p>
 *
 * @see InstitutionRepository
 */
@Entity
@Data
@Table(name = "institution_view")
@Immutable
public class InstitutionEntity {

  @Id
  private UUID id;

  private String name;

  private String officialName;

  private String type;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "institution_region_view",
    joinColumns = @JoinColumn(name = "institution_id"),
    inverseJoinColumns = @JoinColumn(name = "region_id")
  )
  private Set<RegionEntity> regions = new HashSet<>();
}
