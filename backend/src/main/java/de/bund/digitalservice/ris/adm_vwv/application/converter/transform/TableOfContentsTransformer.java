package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Reference;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Analysis;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.ImplicitReferenceType;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.OtherReferences;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for 'Gliederung'.
 */
@RequiredArgsConstructor
public class TableOfContentsTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code TableOfContents} object to a list of references.
   *
   * @return List of table of contents entries or empty list
   */
  public List<String> transform() {
    return List.of();
  }
}
