package de.bund.digitalservice.ris.adm_vwv.application.converter.transform;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.LookupTablesPersistenceService;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.RisMetadata;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Transformer for document type (in German 'Dokumenttyp').
 */
@RequiredArgsConstructor
@Component
public class DocumentTypeTransformer {

  private final LookupTablesPersistenceService lookupTablesPersistenceService;

  /**
   * Transforms the {@code AkomaNtoso} object to a document type.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The document type, or {@code null} if the surrounding {@code <proprietary>}
   *         element is {@code null}
   */
  public DocumentType transform(@Nonnull AkomaNtoso akomaNtoso) {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMetadata)
      .map(RisMetadata::getDocumentType)
      .flatMap(risDocumentType ->
        lookupTablesPersistenceService.findDocumentTypeByAbbreviation(
          risDocumentType.getCategory(),
          DocumentCategory.VERWALTUNGSVORSCHRIFTEN
        )
      )
      .orElse(null);
  }
}
