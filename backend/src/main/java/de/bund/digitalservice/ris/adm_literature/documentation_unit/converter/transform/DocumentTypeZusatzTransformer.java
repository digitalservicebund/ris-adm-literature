package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisDocumentType;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisMeta;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Transformer for date to quote (in German 'Zitierdatum').
 */
@RequiredArgsConstructor
public class DocumentTypeZusatzTransformer {

  private final AkomaNtoso akomaNtoso;

  /**
   * Transforms the {@code AkomaNtoso} object to a longtitle string.
   *
   * @return The longtitle, or {@code null} if it does not exist
   */
  public String transform() {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMeta)
      .map(RisMeta::getDocumentType)
      .map(RisDocumentType::getLongTitle)
      .orElse(null);
  }
}
