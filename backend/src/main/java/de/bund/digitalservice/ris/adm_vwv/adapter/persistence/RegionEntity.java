package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Region JPA entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-vwv}.
 * </p>
 *
 * @see RegionRepository
 */
@Entity
@Data
@Table(name = "region_view")
@Immutable
public class RegionEntity {

  @Id
  private UUID id;

  private String code;

  private String longText;
}
