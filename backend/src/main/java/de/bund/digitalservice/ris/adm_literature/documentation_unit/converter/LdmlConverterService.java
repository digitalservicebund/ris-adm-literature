package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform.*;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LDML converter service for transforming XML/LDML into Business Models.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LdmlConverterService {

  private final XmlReader xmlReader;
  private final FundstellenTransformer fundstellenTransformer;
  private final DocumentTypeTransformer documentTypeTransformer;
  private final NormgeberTransformer normgeberTransformer;
  private final FieldsOfLawTransformer fieldsOfLawTransformer;
  private final KurzreferatTransformer kurzreferatTransformer;
  private final ActiveCitationsTransformer activeCitationsTransformer;
  private final ActiveReferencesTransformer activeReferencesTransformer;

  /**
   * Converts the xml of the given documentation unit to business models.
   *
   * @param documentationUnit The documentation unit to convert
   * @return Business model representation of given documentation unit's xml
   */
  public AdmDocumentationUnitContent convertToBusinessModel(
    @Nonnull DocumentationUnit documentationUnit
  ) {
    AkomaNtoso akomaNtoso = xmlReader.readXml(documentationUnit.xml());
    log.debug("Read Akoma Ntoso from XML: {}.", akomaNtoso);
    List<String> referenceNumbers = new ReferenceNumbersTransformer(akomaNtoso).transform();
    return new AdmDocumentationUnitContent(
      documentationUnit.id(),
      documentationUnit.documentNumber(),
      fundstellenTransformer.transform(akomaNtoso),
      fieldsOfLawTransformer.transform(akomaNtoso),
      new LongTitleTransformer(akomaNtoso).transform(),
      new KeywordsTransformer(akomaNtoso).transform(),
      new DateToQuoteTransformer(akomaNtoso).transform(),
      new EntryIntoEffectDateTransformer(akomaNtoso).transform(),
      new ExpiryDateTransformer(akomaNtoso).transform(),
      new TableOfContentsTransformer().transform(akomaNtoso),
      kurzreferatTransformer.transform(akomaNtoso),
      referenceNumbers,
      referenceNumbers.isEmpty(),
      documentTypeTransformer.transform(akomaNtoso),
      new DocumentTypeZusatzTransformer(akomaNtoso).transform(),
      activeCitationsTransformer.transform(akomaNtoso),
      activeReferencesTransformer.transform(akomaNtoso),
      new NormReferencesTransformer(akomaNtoso).transform(),
      null,
      normgeberTransformer.transform(akomaNtoso),
      new BerufsbilderTransformer().transform(akomaNtoso),
      new TitelAspekteTransformer().transform(akomaNtoso),
      new DefinitionenTransformer().transform(akomaNtoso)
    );
  }
}
