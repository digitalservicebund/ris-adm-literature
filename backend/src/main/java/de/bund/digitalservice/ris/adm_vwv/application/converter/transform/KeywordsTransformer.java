package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Classification;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Keyword;
import java.util.List;
import java.util.Optional;
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
    Optional<Classification> keywords = Optional.ofNullable(
      akomaNtoso.getDoc().getMeta().getClassification()
    );

    if (keywords.isEmpty()) return List.of();
    else return keywords.get().getKeyword().stream().map(Keyword::getValue).toList();
  }
}
