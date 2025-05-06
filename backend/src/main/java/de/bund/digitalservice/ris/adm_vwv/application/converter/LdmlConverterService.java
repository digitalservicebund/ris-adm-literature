package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_vwv.application.converter.transform.EntryIntoEffectDateTransformer;
import de.bund.digitalservice.ris.adm_vwv.application.converter.transform.ExpirationDateTransformer;
import de.bund.digitalservice.ris.adm_vwv.application.converter.transform.FundstellenTransformer;
import de.bund.digitalservice.ris.adm_vwv.application.converter.transform.LongTitleTransformer;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LDML converter service for transforming Business Models into XML/LDML and vice versa.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LdmlConverterService {

  private final XmlReader xmlReader = new XmlReader();

  /**
   * Converts the xml of the given documentation unit to business models.
   * @param documentationUnit The documentation unit to convert
   * @return Business model representation of given documentation unit's xml
   */
  public DocumentationUnitContent convertToBusinessModel(
    @Nonnull DocumentationUnit documentationUnit
  ) {
    AkomaNtoso akomaNtoso = xmlReader.readXml(documentationUnit.xml());
    log.debug("Read Akoma Ntoso from XML: {}.", akomaNtoso);
    return new DocumentationUnitContent(
      documentationUnit.id(),
      documentationUnit.documentNumber(),
      new FundstellenTransformer(akomaNtoso).transform(),
      List.of(),
      new LongTitleTransformer(akomaNtoso).transform(),
      List.of(),
      null,
      new EntryIntoEffectDateTransformer(akomaNtoso).transform(),
      new ExpirationDateTransformer(akomaNtoso).transform(),
      null,
      null,
      List.of(),
      null,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null
    );
  }
}
