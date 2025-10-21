package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Citation type JPA entity ('Art der Zitierung').
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-literature}.
 * </p>
 *
 * @see DocumentTypeRepository
 */
@Entity
@Data
@Table(name = "citation_type_view")
@Immutable
public class CitationTypeEntity {

  @Id
  private UUID id;

  private String abbreviation;

  private String label;
}
