package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.aktivzitierung.LiteratureReferenceEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.aktivzitierung.LiteratureReferenceRepository;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ObjectToLdmlConverterStrategy;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.xml.DomXmlReader;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.xml.NodeToList;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.LiteratureIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.DocumentReference;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentTypeService;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law.FieldOfLaw;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.InstitutionType;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * LDML publish converter service for transforming business models into XML/LDML keeping migrated content
 * not included in the business model like 'Verwaltungsdaten'.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AdmToLdmlConverterStrategy implements ObjectToLdmlConverterStrategy {

  private final JaxbXmlReader jaxbXmlReader;
  private final JaxbXmlWriter jaxbXmlWriter;
  private final LiteratureReferenceRepository literatureReferenceRepository;
  private final DocumentTypeService documentTypeService;
  private final DomXmlReader domXmlReader = new DomXmlReader();

  @Override
  public boolean supports(DocumentationUnitContent content) {
    return content instanceof AdmDocumentationUnitContent;
  }

  /**
   * Converts the given business model to LDML xml.
   *
   * @param content            The documentation unit content to convert
   * @param previousXmlVersion Previous xml version of the documentation unit if it was once published, if not set to {@code null}
   * @return LDML xml representation of the given documentation unit content
   */
  @Override
  public String convertToLdml(
    @Nonnull DocumentationUnitContent content,
    String previousXmlVersion,
    @Nonnull List<DocumentReference> referencedByList
  ) {
    // This cast is safe because the 'supports' method is checked first
    AdmDocumentationUnitContent admDocumentationUnitContent = (AdmDocumentationUnitContent) content;
    AkomaNtoso akomaNtoso;
    if (previousXmlVersion != null) {
      // If there is a previous version it could be a migrated documented. In that case we have to hold some
      // historic data.
      akomaNtoso = jaxbXmlReader.readXml(previousXmlVersion);
      RisMeta previousRisMeta = akomaNtoso.getDoc().getMeta().getProprietary().getMeta();
      Meta meta = createMeta(admDocumentationUnitContent.documentNumber());
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setHistoricAdministrativeData(previousRisMeta.getHistoricAdministrativeData());
      risMeta.setHistoricAbbreviation(previousRisMeta.getHistoricAbbreviation());
      risMeta.setZuordnungen(previousRisMeta.getZuordnungen());
      risMeta.setRegionen(previousRisMeta.getRegionen());
      normalizeHistoricAdministrativeData(meta);
      akomaNtoso.getDoc().setMeta(meta);
      akomaNtoso.getDoc().setPreface(new Preface());
      akomaNtoso.getDoc().setMainBody(new MainBody());
    } else {
      akomaNtoso = createAkomaNtoso(admDocumentationUnitContent);
    }
    Meta meta = akomaNtoso.getDoc().getMeta();
    setInkrafttretedatum(meta, admDocumentationUnitContent.inkrafttretedatum());
    setAusserkrafttretedatum(meta, admDocumentationUnitContent.ausserkrafttretedatum());
    setZitierdaten(meta, admDocumentationUnitContent.zitierdaten());
    setLangueberschrift(
      akomaNtoso.getDoc().getPreface(),
      admDocumentationUnitContent.langueberschrift()
    );
    setGliederung(meta, admDocumentationUnitContent.gliederung());
    setKurzreferat(akomaNtoso.getDoc().getMainBody(), admDocumentationUnitContent.kurzreferat());
    setNormgeber(meta, admDocumentationUnitContent.normgeberList());
    setClassification(meta, admDocumentationUnitContent.keywords());
    setSachgebiete(meta, admDocumentationUnitContent.fieldsOfLaw());
    setDocumentType(
      meta,
      admDocumentationUnitContent.dokumenttyp(),
      admDocumentationUnitContent.dokumenttypZusatz()
    );
    setAktenzeichen(meta, admDocumentationUnitContent.aktenzeichen());
    setFundstellen(meta, admDocumentationUnitContent.fundstellen());
    setNormReferences(meta, admDocumentationUnitContent.normReferences());
    setCaselawReferences(meta, admDocumentationUnitContent.activeCitations());
    setActiveReferences(meta, admDocumentationUnitContent.activeReferences());
    setIdentification(meta, admDocumentationUnitContent);
    setBerufsbilder(meta, admDocumentationUnitContent.berufsbilder());
    setTitelAspekte(meta, admDocumentationUnitContent.titelAspekte());
    setDefinitionen(meta, admDocumentationUnitContent.definitionen());
    setPassiveReferences(meta, referencedByList);
    return jaxbXmlWriter.writeXml(akomaNtoso);
  }

  private String unescapeHtml(String input) {
    return input == null ? null : Parser.unescapeEntities(input, true);
  }

  private AkomaNtoso createAkomaNtoso(
    @Nonnull AdmDocumentationUnitContent admDocumentationUnitContent
  ) {
    AkomaNtoso akomaNtoso;
    akomaNtoso = new AkomaNtoso();
    Doc doc = new Doc();
    akomaNtoso.setDoc(doc);
    Meta meta = createMeta(admDocumentationUnitContent.documentNumber());
    doc.setMeta(meta);
    doc.setPreface(new Preface());
    doc.setMainBody(new MainBody());
    return akomaNtoso;
  }

  private Meta createMeta(String documentNumber) {
    Meta meta = new Meta();
    Identification identification = new Identification();
    FrbrElement frbrWork = new FrbrElement();
    FrbrAlias frbrAlias = new FrbrAlias();
    frbrAlias.setName("Dokumentnummer");
    frbrAlias.setValue(documentNumber);
    frbrWork.setFrbrAlias(List.of(frbrAlias));
    identification.setFrbrWork(frbrWork);
    meta.setIdentification(identification);
    return meta;
  }

  private void setInkrafttretedatum(Meta meta, String inkrafttretedatum) {
    if (inkrafttretedatum != null) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setEntryIntoEffectDate(inkrafttretedatum);
    }
  }

  private void setAusserkrafttretedatum(Meta meta, String ausserkrafttretedatum) {
    if (ausserkrafttretedatum != null) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setExpiryDate(ausserkrafttretedatum);
    }
  }

  private void setZitierdaten(Meta meta, List<String> zitierdaten) {
    if (CollectionUtils.isNotEmpty(zitierdaten)) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setDateToQuoteList(zitierdaten);
    }
  }

  private void setLangueberschrift(Preface preface, String langueberschrift) {
    LongTitle longTitle = new LongTitle();
    preface.setLongTitle(longTitle);
    JaxbHtml jaxbHtml = new JaxbHtml();
    jaxbHtml.setName("longTitle");
    longTitle.setBlock(jaxbHtml);
    jaxbHtml.setHtml(List.of(unescapeHtml(langueberschrift)));
  }

  private void setGliederung(Meta meta, String gliederung) {
    if (StringUtils.isNotBlank(gliederung)) {
      List<String> entries = Pattern.compile("<p>(.*?)</p>")
        .matcher(gliederung)
        .results()
        .map(matchResult -> unescapeHtml(matchResult.group(1)))
        .toList();
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setTableOfContentsEntries(entries);
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

    // Some files have complex html logic like style="text-align: center;" so we must handle these cases
    // We only allow paragraphs and line breaks.
    Safelist safelist = Safelist.none().addTags("p", "br");
    String cleanHtml = Jsoup.clean(kurzreferat, safelist);

    // Unescape HTML entities like &nbsp; from the cleaned string
    String unescapedHtml = unescapeHtml(cleanHtml);

    String kurzreferatWithAknNs = unescapedHtml
      .replaceAll("<(/?)p>", "<$1akn:p>")
      .replaceAll("<br\\s*/?>", "<akn:br/>");

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
    if (CollectionUtils.isNotEmpty(normgeberList)) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
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
      risMeta.setNormgeber(risNormgeberList);
    }
  }

  private void setClassification(Meta meta, List<String> keywords) {
    if (CollectionUtils.isNotEmpty(keywords)) {
      Classification classification = new Classification();
      meta.setClassification(classification);
      classification.setKeyword(
        keywords
          .stream()
          .map(keywordValue -> {
            Keyword keyword = new Keyword();
            String sanitizedKeyword = unescapeHtml(keywordValue);
            keyword.setShowAs(sanitizedKeyword);
            keyword.setValue(sanitizedKeyword);
            return keyword;
          })
          .toList()
      );
    }
  }

  private void setSachgebiete(Meta meta, List<FieldOfLaw> fieldsOfLaw) {
    if (CollectionUtils.isNotEmpty(fieldsOfLaw)) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setFieldsOfLaw(
        fieldsOfLaw
          .stream()
          .map(fieldOfLaw -> {
            RisFieldOfLaw risFieldOfLaw = new RisFieldOfLaw();
            risFieldOfLaw.setValue(fieldOfLaw.identifier());
            risFieldOfLaw.setNotation(fieldOfLaw.notation());
            return risFieldOfLaw;
          })
          .toList()
      );
    }
  }

  private void setDocumentType(Meta meta, DocumentType dokumenttyp, String dokumenttypZusatz) {
    RisDocumentType risDocumentType = new RisDocumentType();
    risDocumentType.setCategory(dokumenttyp.abbreviation());
    String value = dokumenttyp.abbreviation();
    String sanitizedZusatz = unescapeHtml(dokumenttypZusatz);
    if (StringUtils.isNotBlank(sanitizedZusatz)) {
      risDocumentType.setLongTitle(sanitizedZusatz);
      value += " " + sanitizedZusatz;
    }
    risDocumentType.setValue(value);
    meta.getOrCreateProprietary().getMeta().setDocumentType(risDocumentType);
  }

  private void setAktenzeichen(Meta meta, List<String> aktenzeichen) {
    if (CollectionUtils.isNotEmpty(aktenzeichen)) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setReferenceNumbers(aktenzeichen.stream().map(this::unescapeHtml).toList());
    }
  }

  private void setFundstellen(Meta meta, List<Fundstelle> fundstellen) {
    if (CollectionUtils.isNotEmpty(fundstellen)) {
      OtherReferences otherReferences = meta.getOrCreateAnalysis().getOtherReferences().getFirst();
      otherReferences
        .getImplicitReferences()
        .addAll(
          fundstellen
            .stream()
            .map(fundstelle -> {
              ImplicitReference implicitReference = new ImplicitReference();
              String zitatstelle = fundstelle.zitatstelle();
              if (fundstelle.periodikum() == null) {
                // Can occur due to ambiguous Periodika. User have to resolve it manually.
                implicitReference.setShortForm(fundstelle.ambiguousPeriodikum());
                implicitReference.setShowAs(fundstelle.ambiguousPeriodikum() + ", " + zitatstelle);
              } else {
                implicitReference.setShortForm(fundstelle.periodikum().abbreviation());
                implicitReference.setShowAs(
                  fundstelle.periodikum().abbreviation() + ", " + zitatstelle
                );
                // RISDEV-8915 persist public id reference for resolving ambiguous periodika
                RisFundstelle risFundstelle = new RisFundstelle();
                risFundstelle.setAbbreviation(fundstelle.periodikum().abbreviation());
                risFundstelle.setZitatstelle(zitatstelle);
                risFundstelle.setPublicId(fundstelle.periodikum().publicId());
                implicitReference.setFundstelle(risFundstelle);
              }
              return implicitReference;
            })
            .toList()
        );
    }
  }

  private void setNormReferences(Meta meta, List<NormReference> normReferences) {
    if (CollectionUtils.isNotEmpty(normReferences)) {
      OtherReferences otherReferences = meta.getOrCreateAnalysis().getOtherReferences().getFirst();
      otherReferences
        .getImplicitReferences()
        .addAll(
          normReferences
            .stream()
            .flatMap(normReference -> {
              if (CollectionUtils.isNotEmpty(normReference.singleNorms())) {
                return normReference
                  .singleNorms()
                  .stream()
                  .map(singleNorm -> {
                    ImplicitReference implicitReference = new ImplicitReference();
                    String abbreviation = normReference.normAbbreviation().abbreviation();
                    implicitReference.setShortForm(abbreviation);
                    implicitReference.setShowAs(abbreviation + " " + singleNorm.singleNorm());
                    RisNormReference risNormReference = new RisNormReference();
                    risNormReference.setSingleNorm(singleNorm.singleNorm());
                    risNormReference.setDateOfVersion(singleNorm.dateOfVersion());
                    risNormReference.setDateOfRelevance(singleNorm.dateOfRelevance());
                    implicitReference.setNormReference(risNormReference);
                    return implicitReference;
                  });
              }
              ImplicitReference implicitReference = new ImplicitReference();
              String abbreviation = normReference.normAbbreviation().abbreviation();
              implicitReference.setShortForm(abbreviation);
              implicitReference.setShowAs(abbreviation);
              return Stream.of(implicitReference);
            })
            .toList()
        );
    }
  }

  private void setCaselawReferences(Meta meta, List<ActiveCitation> activeCitations) {
    if (CollectionUtils.isNotEmpty(activeCitations)) {
      OtherReferences otherReferences = meta.getOrCreateAnalysis().getOtherReferences().getFirst();
      otherReferences
        .getImplicitReferences()
        .addAll(
          activeCitations
            .stream()
            .map(activeCitation -> {
              ImplicitReference implicitReference = new ImplicitReference();
              String shortForm = StringUtils.joinWith(
                " ",
                activeCitation.zitierArt().abbreviation(),
                activeCitation.court().type(),
                activeCitation.court().location(),
                activeCitation.fileNumber()
              );
              implicitReference.setShortForm(shortForm);
              implicitReference.setShowAs(shortForm + " " + activeCitation.decisionDate());
              RisCaselawReference caselawReference = new RisCaselawReference();
              caselawReference.setAbbreviation(activeCitation.zitierArt().abbreviation());
              caselawReference.setCourt(activeCitation.court().type());
              caselawReference.setCourtLocation(activeCitation.court().location());
              caselawReference.setDate(activeCitation.decisionDate());
              caselawReference.setDocumentNumber(activeCitation.documentNumber());
              caselawReference.setReferenceNumber(activeCitation.fileNumber());
              implicitReference.setCaselawReference(caselawReference);
              return implicitReference;
            })
            .toList()
        );
    }
  }

  private void setActiveReferences(Meta meta, List<ActiveReference> activeReferences) {
    if (CollectionUtils.isNotEmpty(activeReferences)) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setActiveReferences(
        activeReferences
          .stream()
          .flatMap(activeReference -> {
            if (CollectionUtils.isNotEmpty(activeReference.singleNorms())) {
              return activeReference
                .singleNorms()
                .stream()
                .map(singleNorm -> {
                  RisActiveReference risActiveReference = new RisActiveReference();
                  risActiveReference.setTypeNumber(activeReference.verweisTyp().typNummer());
                  risActiveReference.setReference(
                    activeReference.normAbbreviation().abbreviation()
                  );
                  risActiveReference.setParagraph(singleNorm.singleNorm());
                  risActiveReference.setDateOfVersion(singleNorm.dateOfVersion());
                  return risActiveReference;
                });
            }
            RisActiveReference risActiveReference = new RisActiveReference();
            risActiveReference.setTypeNumber(activeReference.verweisTyp().typNummer());
            risActiveReference.setReference(activeReference.normAbbreviation().abbreviation());
            return Stream.of(risActiveReference);
          })
          .toList()
      );
    }
  }

  private void setBerufsbilder(Meta meta, List<String> berufsbilder) {
    if (CollectionUtils.isNotEmpty(berufsbilder)) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setBerufsbilder(berufsbilder);
    }
  }

  private void setTitelAspekte(Meta meta, List<String> titelAspekte) {
    if (CollectionUtils.isNotEmpty(titelAspekte)) {
      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setTitelAspekte(titelAspekte);
    }
  }

  private void setDefinitionen(Meta meta, List<Definition> definitionen) {
    if (CollectionUtils.isNotEmpty(definitionen)) {
      List<RisDefinition> risDefinitionen = definitionen
        .stream()
        .map(definition -> {
          RisDefinition risDefinition = new RisDefinition();
          risDefinition.setBegriff(definition.begriff());
          return risDefinition;
        })
        .toList();

      RisMeta risMeta = meta.getOrCreateProprietary().getMeta();
      risMeta.setDefinitionen(risDefinitionen);
    }
  }

  private void setPassiveReferences(Meta meta, List<DocumentReference> referencedByList) {
    if (CollectionUtils.isNotEmpty(referencedByList)) {
      OtherReferences otherReferences = meta.getOrCreateAnalysis().getOtherReferences().getFirst();
      otherReferences
        .getImplicitReferences()
        .addAll(
          referencedByList
            .stream()
            .map(referencedBy -> {
              RisReferenz risReferenz = new RisReferenz();
              risReferenz.setRichtung(new RisDomainTerm("Richtung der Referenzierung", "passiv"));
              risReferenz.setReferenzArt(new RisDomainTerm("Art der Referenz", "sli"));
              risReferenz.setDokumentnummer(
                new RisDomainTerm("Dokumentnummer", referencedBy.documentNumber())
              );
              risReferenz.setRelativerPfad(
                new RisDomainTerm("Pfad zur Referenz", referencedBy.documentNumber())
              );
              switch (referencedBy.documentCategory()) {
                case LITERATUR_SELBSTAENDIG -> {
                  LiteratureIndex literatureIndex = literatureReferenceRepository
                    .findById(referencedBy.documentNumber())
                    .map(LiteratureReferenceEntity::getLiteratureIndex)
                    .orElseThrow();
                  String dokumenttypAbkuerzungen = String.join(
                    ", ",
                    literatureIndex.getDokumenttypen()
                  );
                  risReferenz.setDokumenttypAbkuerzung(
                    new RisDomainTerm("Dokumenttyp, abgekürzt", dokumenttypAbkuerzungen)
                  );
                  final Map<String, String> documentTypeNames =
                    documentTypeService.getDocumentTypeNames();
                  String dokumenttyp = literatureIndex
                    .getDokumenttypen()
                    .stream()
                    .map(documentTypeNames::get)
                    .collect(Collectors.joining(", "));
                  risReferenz.setDokumenttyp(new RisDomainTerm("Dokumenttyp", dokumenttyp));
                  String titel = literatureIndex.getTitel();
                  risReferenz.setTitel(new RisDomainTerm("Titel", titel));
                  String verfasser = String.join(", ", literatureIndex.getVerfasserList());
                  risReferenz.setAutor(new RisDomainTerm("Autor(en)", verfasser));
                  risReferenz.setVeroeffentlichungsjahr(
                    new RisDomainTerm(
                      "Veröffentlichungsjahr",
                      literatureIndex.getVeroeffentlichungsjahr()
                    )
                  );
                  risReferenz.setStandardzusammenfassung(
                    String.join(
                      ", ",
                      verfasser,
                      titel,
                      dokumenttypAbkuerzungen,
                      literatureIndex.getVeroeffentlichungsjahr()
                    )
                  );
                }
                case LITERATUR,
                  LITERATUR_UNSELBSTAENDIG,
                  VERWALTUNGSVORSCHRIFTEN -> throw new IllegalStateException("Not supported");
              }
              ImplicitReference implicitReference = new ImplicitReference();
              implicitReference.setReference(risReferenz);
              return implicitReference;
            })
            .toList()
        );
    }
  }

  private void normalizeHistoricAdministrativeData(Meta meta) {
    JaxbHtml historicAdministrativeData = meta
      .getOrCreateProprietary()
      .getMeta()
      .getHistoricAdministrativeData();
    if (historicAdministrativeData != null) {
      List<?> nodes = historicAdministrativeData.getHtml();
      List<Node> filteredNodes = nodes
        .stream()
        .filter(Node.class::isInstance)
        .map(Node.class::cast)
        .filter(node -> !(node.getNodeType() == Node.TEXT_NODE && node.getTextContent().isBlank()))
        .toList();
      filteredNodes.forEach(this::removeBlankTextNodes);
      historicAdministrativeData.setHtml(filteredNodes);
    }
  }

  private void removeBlankTextNodes(Node node) {
    for (Node childNode : NodeToList.toList(node.getChildNodes())) {
      if (childNode.getNodeType() == Node.TEXT_NODE && childNode.getTextContent().isBlank()) {
        node.removeChild(childNode);
      } else {
        removeBlankTextNodes(childNode);
      }
    }
  }

  private void setIdentification(
    Meta meta,
    AdmDocumentationUnitContent admDocumentationUnitContent
  ) {
    Identification identification = new IdentificationConverter()
      .convert(
        admDocumentationUnitContent,
        meta.getOrCreateProprietary().getMeta().getZuordnungen()
      );
    meta.setIdentification(identification);
  }
}
