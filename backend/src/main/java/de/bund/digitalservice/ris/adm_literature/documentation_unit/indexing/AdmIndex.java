package de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.List;
import lombok.Data;

/**
 * Adm index.
 */
@Embeddable
@Data
public class AdmIndex {

  @Basic
  private String langueberschrift;

  @Basic
  private String fundstellenCombined;

  @Column(columnDefinition = "text[]")
  private List<String> fundstellen;

  @Basic
  private String inkrafttretedatum;

  @Basic
  private String zitierdatenCombined;

  @Column(columnDefinition = "text[]")
  private List<String> zitierdaten;

  @Basic
  private String normgeberListCombined;

  @Column(columnDefinition = "text[]")
  private List<String> normgeberList;

  @Basic
  private String aktenzeichenListCombined;

  @Column(columnDefinition = "text[]")
  private List<String> aktenzeichenList;

  @Basic
  private String dokumenttyp;
}
