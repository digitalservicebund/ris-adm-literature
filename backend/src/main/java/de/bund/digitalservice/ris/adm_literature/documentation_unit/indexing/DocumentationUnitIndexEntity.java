package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Documentation unit index JPA entity.
 */
@Entity
@Data
@EqualsAndHashCode(exclude = "documentationUnit")
@ToString(exclude = "documentationUnit")
@Table(name = "documentation_unit_index")
public class DocumentationUnitIndexEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @OneToOne(optional = false)
  private DocumentationUnitEntity documentationUnit;

  @Basic
  private String fundstellenCombined;

  @Column(columnDefinition = "text[]")
  private List<String> fundstellen;

  @Embedded
  private AdmIndex admIndex;

  @Embedded
  private LiteratureIndex literatureIndex;

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
