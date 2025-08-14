package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.InstitutionType;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.Normgeber;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.*;
import de.bund.digitalservice.ris.adm_vwv.application.converter.ldml.adapter.NodeToList;
import de.bund.digitalservice.ris.adm_vwv.application.converter.transform.*;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * LDML converter service for transforming Business Models into XML/LDML and vice versa.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LdmlConverterService {

  private final XmlReader xmlReader;
  private final XmlWriter xmlWriter;
  private final DomXmlReader domXmlReader = new DomXmlReader();
  private final FundstellenTransformer fundstellenTransformer;
  private final DocumentTypeTransformer documentTypeTransformer;
  private final NormgeberTransformer normgeberTransformer;
  private final FieldsOfLawTransformer fieldsOfLawTransformer;
  private final KurzreferatTransformer kurzreferatTransformer;

  /**
   * Converts the xml of the given documentation unit to business models.
   *
   * @param documentationUnit The documentation unit to convert
   * @return Business model representation of given documentation unit's xml
   */
  public DocumentationUnitContent convertToBusinessModel(
    @Nonnull DocumentationUnit documentationUnit
  ) {
    AkomaNtoso akomaNtoso = xmlReader.readXml(documentationUnit.xml());
    log.debug("Read Akoma Ntoso from XML: {}.", akomaNtoso);
    List<String> referenceNumbers = new ReferenceNumbersTransformer(akomaNtoso).transform();
    return new DocumentationUnitContent(
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
      new ActiveCitationsTransformer(akomaNtoso).transform(),
      new ActiveReferencesTransformer(akomaNtoso).transform(),
      new NormReferencesTransformer(akomaNtoso).transform(),
      null,
      normgeberTransformer.transform(akomaNtoso)
    );
  }

  /**
   * Converts the given business model to LDML xml.
   *
   * @param documentationUnitContent The documentation unit content to convert
   * @param previousXmlVersion       Previous xml version of the documentation unit if it was once published, if not set to {@code null}
   * @return LDML xml representation of the given documentation unit content
   */
  public String convertToLdml(
    @Nonnull DocumentationUnitContent documentationUnitContent,
    String previousXmlVersion
  ) {
    AkomaNtoso akomaNtoso;
    if (previousXmlVersion != null) {
      // If there is a previous version it could be a migrated documented. In that case we have to hold some
      // historic data.
      akomaNtoso = xmlReader.readXml(previousXmlVersion);
    } else {
      akomaNtoso = new AkomaNtoso();
      Doc doc = new Doc();
      akomaNtoso.setDoc(doc);
      Meta meta = new Meta();
      Identification identification = new Identification();
      FrbrElement frbrExpression = new FrbrElement();
      FrbrAlias frbrAlias = new FrbrAlias();
      frbrAlias.setName("documentNumber");
      frbrAlias.setValue(documentationUnitContent.documentNumber());
      frbrExpression.setFrbrAlias(List.of(frbrAlias));
      identification.setFrbrExpression(frbrExpression);
      meta.setIdentification(identification);
      doc.setMeta(meta);
      doc.setPreface(new Preface());
      doc.setMainBody(new MainBody());
    }
    Meta meta = akomaNtoso.getDoc().getMeta();
    setInkrafttretedatum(meta, documentationUnitContent.inkrafttretedatum());
    setAusserkrafttretedatum(meta, documentationUnitContent.ausserkrafttretedatum());
    setZitierdaten(meta, documentationUnitContent.zitierdaten());
    setLangueberschrift(
      akomaNtoso.getDoc().getPreface(),
      documentationUnitContent.langueberschrift()
    );
    setGliederung(meta, documentationUnitContent.gliederung());
    setKurzreferat(akomaNtoso.getDoc().getMainBody(), documentationUnitContent.kurzreferat());
    setNormgeber(meta, documentationUnitContent.normgeberList());
    return xmlWriter.writeXml(akomaNtoso);
  }

  private void setInkrafttretedatum(Meta meta, String inkrafttretedatum) {
    if (inkrafttretedatum != null) {
      RisMetadata risMetadata = meta.getOrCreateProprietary().getMetadata();
      risMetadata.setEntryIntoEffectDate(inkrafttretedatum);
    }
  }

  private void setAusserkrafttretedatum(Meta meta, String ausserkrafttretedatum) {
    if (ausserkrafttretedatum != null) {
      RisMetadata risMetadata = meta.getOrCreateProprietary().getMetadata();
      risMetadata.setExpiryDate(ausserkrafttretedatum);
    }
  }

  private void setZitierdaten(Meta meta, List<String> zitierdaten) {
    if (!zitierdaten.isEmpty()) {
      RisMetadata risMetadata = meta.getOrCreateProprietary().getMetadata();
      risMetadata.setDateToQuoteList(zitierdaten);
    }
  }

  private void setLangueberschrift(Preface preface, String langueberschrift) {
    LongTitle longTitle = new LongTitle();
    preface.setLongTitle(longTitle);
    JaxbHtml jaxbHtml = new JaxbHtml();
    jaxbHtml.setName("longTitle");
    longTitle.setBlock(jaxbHtml);
    jaxbHtml.setHtml(List.of(langueberschrift));
  }

  private void setGliederung(Meta meta, String gliederung) {
    if (StringUtils.isNotBlank(gliederung)) {
      List<String> entries = Pattern.compile("<p>(.*?)</p>")
        .matcher(gliederung)
        .results()
        .map(matchResult -> matchResult.group(1))
        .toList();
      RisMetadata risMetadata = meta.getOrCreateProprietary().getMetadata();
      risMetadata.setTableOfContentsEntries(entries);
    }
  }

  private void setKurzreferat(MainBody mainBody, String kurzreferat) {
    if (StringUtils.isBlank(kurzreferat)) {
      JaxbHtml hcontainer = new JaxbHtml();
      hcontainer.setName("crossheading");
      mainBody.setHcontainer(hcontainer);
      return;
    }
    JaxbHtml div = new JaxbHtml();
    mainBody.setDiv(div);
    // As our editor do not offer any formatting, it is sufficient to replace <p> elements
    String kurzreferatWithAknNs = kurzreferat.replaceAll("<(/?)p>", "<$1akn:p>");
    Node node = domXmlReader.readXml("<div>" + kurzreferatWithAknNs + "</div>");
    div.setHtml(
      NodeToList.toList(node.getChildNodes())
        .stream()
        .map(childNode -> {
          if (childNode instanceof Element element) {
            element.setAttributeNS(
              "http://www.w3.org/2000/xmlns/",
              "xmlns:akn",
              XmlNamespace.AKN_NS
            );
            return childNode;
          } else if (
            childNode.getNodeType() == Node.TEXT_NODE &&
            StringUtils.isNotBlank(childNode.getTextContent())
          ) {
            // We only add non-empty text nodes
            return childNode.getTextContent();
          }
          // In case there would be processing instructions or comments
          return null;
        })
        .filter(Objects::nonNull)
        .toList()
    );
  }

  private void setNormgeber(Meta meta, List<Normgeber> normgeberList) {
    if (!normgeberList.isEmpty()) {
      RisMetadata risMetadata = meta.getOrCreateProprietary().getMetadata();
      List<RisNormgeber> risNormgeberList = normgeberList
        .stream()
        .map(normgeber -> {
          RisNormgeber risNormgeber = new RisNormgeber();
          if (normgeber.institution().type() == InstitutionType.INSTITUTION) {
            risNormgeber.setOrgan(normgeber.institution().name());
            risNormgeber.setStaat(normgeber.regions().getFirst().code());
          } else {
            risNormgeber.setStaat(normgeber.institution().name());
          }
          return risNormgeber;
        })
        .toList();
      risMetadata.setNormgeber(risNormgeberList);
    }
  }
}
