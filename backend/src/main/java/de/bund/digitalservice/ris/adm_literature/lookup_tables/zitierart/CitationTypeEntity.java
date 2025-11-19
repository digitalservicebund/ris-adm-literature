package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import jakarta.persistence.*;
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
 * @see CitationTypeRepository
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

  @Enumerated(EnumType.STRING)
  private DocumentCategory documentCategory;
}
