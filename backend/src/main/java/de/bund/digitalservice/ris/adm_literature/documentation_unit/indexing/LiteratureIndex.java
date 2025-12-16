package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.List;
import lombok.Data;

/**
 * Literature index.
 */
@Data
@Embeddable
public class LiteratureIndex {

  @Basic
  private String titel;

  @Basic
  private String veroeffentlichungsjahr;

  @Column(columnDefinition = "text[]")
  private List<String> dokumenttypen;

  @Basic
  private String dokumenttypenCombined;

  @Column(columnDefinition = "text[]")
  private List<String> verfasserList;

  @Basic
  private String verfasserListCombined;
}
