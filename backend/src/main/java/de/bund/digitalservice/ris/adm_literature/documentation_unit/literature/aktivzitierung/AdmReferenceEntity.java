package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.AdmIndex;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Adm reference entity points to a view in the adm database schema (adm 'RefView').
 * <p>
 *   This entity is only for reading. The ownership for the table has schema 'adm'.
 * </p>
 */
@Entity
@Table(name = "adm_reference_view", schema = "adm")
@Data
public class AdmReferenceEntity {

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
