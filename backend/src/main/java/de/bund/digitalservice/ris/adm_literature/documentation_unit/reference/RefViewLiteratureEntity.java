package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.LiteratureIndex;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Ref view literature entity points to a view in the references database schema.
 * <p>
 *   This entity is only for reading. The ownership for the table has schema 'references_schema'.
 * </p>
 */
@Entity
@Table(name = "ref_view_literature", schema = "references_schema")
@Data
@Immutable
public class RefViewLiteratureEntity {

  private UUID id;

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
