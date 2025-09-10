package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Legal Periodical JPA entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-vwv}.
 * </p>
 *
 * @see LegalPeriodicalsRepository
 */
@Entity
@Data
@Table(name = "legal_periodical_view")
@Immutable
public class LegalPeriodicalEntity {

  @Id
  private UUID id;

  private String abbreviation;

  private String title;

  private String subtitle;

  private String citationStyle;

  private String jurisId;

  private String publicId;
}
