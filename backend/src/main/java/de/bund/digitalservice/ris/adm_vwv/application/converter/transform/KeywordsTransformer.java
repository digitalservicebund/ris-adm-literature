package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Keyword;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Expiry date transformer.
 */
@RequiredArgsConstructor
public class KeywordsTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code Keyword} object to a string.
   *
   * @return The list of keyword values or an empty list
   */
  public List<String> transform() {
    List<Keyword> keywords = akomaNtoso.getDoc().getMeta().getClassification().getKeyword();
    return keywords.stream().map(Keyword::getValue).toList();
  }
}
