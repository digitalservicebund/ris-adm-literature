package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Court JPA entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-literature}.
 * </p>
 *
 * @see CourtRepository
 */
@Entity
@Data
@Table(name = "court_view")
@Immutable
public class CourtEntity {

  @Id
  private UUID id;

  private String type;

  private String location;

  private String jurisId;
}
