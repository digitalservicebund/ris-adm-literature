package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Document type entity.
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-vwv}.
 * </p>
 *
 * @see DocumentTypesRepository
 */
@Entity
@Data
@Table(name = "document_types_view")
public class DocumentTypeEntity {

  @Id
  private UUID id;

  private String abbreviation;

  private String name;
}
