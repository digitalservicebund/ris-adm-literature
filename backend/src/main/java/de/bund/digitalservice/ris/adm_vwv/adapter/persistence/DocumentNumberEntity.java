package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

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
@Table(
  name = "document_number",
  uniqueConstraints = { @UniqueConstraint(columnNames = { "year", "document_type_code" }) }
)
public class DocumentNumberEntity {

  @Id
  @GeneratedValue
  private UUID id;

  /**
   * The latest document number used. The complete string is persisted here,
   * e.g. {@code KSNR2025000001} or {@code KALU2025000001}.
   */
  @Basic(optional = false)
  private String latest;

  @Basic(optional = false)
  private Year year;

  /**
   * The type of the document, which determines the prefix and numbering sequence.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "document_type_code", nullable = false)
  private DocumentTypeCode documentTypeCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "documentation_office", nullable = false)
  private DocumentationOffice documentationOffice;
}
