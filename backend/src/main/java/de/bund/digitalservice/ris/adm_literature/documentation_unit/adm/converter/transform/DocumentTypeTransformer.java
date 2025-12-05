package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.transform;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.RisMeta;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentTypeService;
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

  private final DocumentTypeService documentTypeService;

  /**
   * Transforms the {@code AkomaNtoso} object to a document type.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return The document type, or {@code null} if the surrounding {@code <proprietary>}
   *         element is {@code null}
   */
  public DocumentType transform(@Nonnull AkomaNtoso akomaNtoso) {
    return Optional.ofNullable(akomaNtoso.getDoc().getMeta().getProprietary())
      .map(Proprietary::getMeta)
      .map(RisMeta::getDocumentType)
      .flatMap(risDocumentType ->
        documentTypeService.findDocumentTypeByAbbreviation(
          risDocumentType.getCategory(),
          DocumentCategory.VERWALTUNGSVORSCHRIFTEN
        )
      )
      .orElse(null);
  }
}
