package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import jakarta.persistence.*;
import java.time.Year;
import java.util.UUID;
import lombok.Data;

/**
 * Document number entity.
 * <p>
 *   This entity is used to handle concurrent threads on creating a {@link DocumentationUnitEntity} with
 *   an entity lock.
 * </p>
 * @see DocumentNumberRepository
 */
@Entity
@Data
@Table(name = "document_number")
public class DocumentNumberEntity {

  @Id
  @GeneratedValue
  private UUID id;

  /**
   * The latest document number used. The complete string is persisted here, e.g. {@code KSNR2025000001}.
   */
  @Basic(optional = false)
  private String latest;

  @Basic(optional = false)
  private Year year;

  /**
   * The document series prefix, e.g., "KALU", "KSNR".
   */
  @Basic(optional = false)
  private String prefix;
}
