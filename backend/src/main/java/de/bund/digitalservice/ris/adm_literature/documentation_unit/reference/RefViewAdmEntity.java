package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.AdmIndex;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Ref view adm entity points to a view in the adm database schema.
 * <p>
 *   This entity is only for reading. The ownership for the table has schema 'references_schema'.
 * </p>
 */
@Entity
@Table(name = "ref_view_adm", schema = "references_schema")
@Data
public class RefViewAdmEntity {

  private UUID id;

  @Id
  private String documentNumber;

  @Enumerated(EnumType.STRING)
  private DocumentationOffice documentationOffice;

  @Embedded
  private AdmIndex admIndex;

  /**
   * Returns an instance of {@link AdmIndex}. If the current instance is {@code null} then
   * a new instance is created, assigned and returned.
   * @return Instance of {@code AdmIndex}
   */
  @Nonnull
  public AdmIndex getAdmIndex() {
    if (admIndex == null) {
      admIndex = new AdmIndex();
    }
    return admIndex;
  }
}
