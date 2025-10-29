package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Document type JPA entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-literature}.
 * </p>
 *
 * @see DocumentTypeRepository
 */
@Entity
@Data
@Table(name = "document_type_view")
@Immutable
public class DocumentTypeEntity {

  @Id
  private UUID id;

  private String abbreviation;

  private String name;

  @Enumerated(EnumType.STRING)
  private DocumentCategory documentCategory;
}
