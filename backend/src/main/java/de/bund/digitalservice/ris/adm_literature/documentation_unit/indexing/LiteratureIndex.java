package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
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

  @Basic
  private String dokumenttypen;

  @Basic
  private String verfasser;
}
