package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.aktivzitierung;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.LiteratureIndex;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Literature reference entity points to a view in the literature database schema (literature 'RefView').
 * <p>
 *   This entity is only for reading. The ownership for the table has schema 'literature'.
 * </p>
 */
@Entity
@Table(name = "literature_reference_view", schema = "literature")
@Data
public class LiteratureReferenceEntity {

  @Id
  private String documentNumber;

  @Enumerated(EnumType.STRING)
  private DocumentationOffice documentationOffice;

  @Embedded
  private LiteratureIndex literatureIndex;

  /**
   * Returns an instance of {@link LiteratureIndex}. If the current instance is {@code null} then
   * a new instance is created, assigned and returned.
   * @return Instance of {@code LiteratureIndex}
   */
  @Nonnull
  public LiteratureIndex getLiteratureIndex() {
    if (literatureIndex == null) {
      literatureIndex = new LiteratureIndex();
    }
    return literatureIndex;
  }
}
