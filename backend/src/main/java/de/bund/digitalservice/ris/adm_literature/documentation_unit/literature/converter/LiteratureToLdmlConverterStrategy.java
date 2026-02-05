package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.converter;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungAdm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungRechtsprechung;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungSli;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungUli;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ObjectToLdmlConverterStrategy;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.xml.DomXmlWriter;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.LiteratureDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.DocumentReference;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Unified LDML publish converter service for Literature (SLI and ULI).
 * <p>
 * Matches the architecture of XmlItemProcessor:
 * 1. Extracts/Normalizes context into a Record
 * 2. Runs a transformation pipeline
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LiteratureToLdmlConverterStrategy implements ObjectToLdmlConverterStrategy {

  private final LdmlDocumentFactory ldmlDocumentFactory;

  private static final String VALUE = "value";
  private static final String SOURCE = "source";
  private static final String UNDEFINED = "attributsemantik-noch-undefiniert";
  private static final String ACTIVE = "active";
  private static final String SHOW_AS = "showAs";
  private static final String OTHER_REFERENCES = "akn:otherReferences";
  private static final String IMPLICIT_REFERENCE = "akn:implicitReference";
  private static final String DOCUMENT_NUMBER = "documentNumber";
  private static final String DOKUMENT_TYP = "dokumenttyp";

  @Override
  public boolean supports(DocumentationUnitContent content) {
    return (
      content instanceof UliDocumentationUnitContent ||
      content instanceof SliDocumentationUnitContent
    );
  }

  @Override
  public String convertToLdml(
    @Nonnull DocumentationUnitContent documentationUnitContent,
    String previousXmlVersion,
    @Nonnull List<DocumentReference> referencedByList
  ) {
    try {
      if (previousXmlVersion != null) {
        // We'd need an LdmlDocument parser here if we want to edit
        // For now, just create a new one
        log.warn("Editing previous XML version is not fully implemented; creating new document.");
      }

      LiteratureDocumentCategory category =
        switch (documentationUnitContent) {
          case UliDocumentationUnitContent _ -> LiteratureDocumentCategory.ULI;
          case SliDocumentationUnitContent _ -> LiteratureDocumentCategory.SLI;
          default -> throw new IllegalStateException(
            "Unexpected content type: " + documentationUnitContent.getClass()
          );
        };

      LiteratureDocumentationUnitContent content =
        (LiteratureDocumentationUnitContent) documentationUnitContent;

      LdmlDocument ldmlDocument = ldmlDocumentFactory.createDocument(category);
      transformToLdml(ldmlDocument, content, category);

      return DomXmlWriter.xmlToString(ldmlDocument.getDocument());
    } catch (Exception e) {
      log.error("Failed to convert Literature content to LDML", e);
      throw new PublishingFailedException("Failed to convert Literature content to LDML", e);
    }
  }

  // Order should be the same as in the migration project
  private void transformToLdml(
    LdmlDocument ldmlDocument,
    LiteratureDocumentationUnitContent data,
    LiteratureDocumentCategory category
  ) {
    // shared logic
    mapDocumentNumber(ldmlDocument, data.documentNumber());
    mapDokumentart(ldmlDocument, category);
    mapVeroeffentlichungsJahre(ldmlDocument, data.veroeffentlichungsjahr());
    mapTitles(ldmlDocument, data);
    mapClassifications(ldmlDocument, data.dokumenttypen());
    mapNote(ldmlDocument, data.note());
    mapKurzreferat(ldmlDocument);

    // SLI specific logic
    // This could possibly have the same logic as ULI and doesn't need to be an if case
    // Should be checked when implementing ULI Aktivzitierung
    if (data instanceof SliDocumentationUnitContent sliData) {
      mapAktivzitierungSelbstaendigeLiteratur(ldmlDocument, sliData.aktivzitierungenSli());
      mapAktivzitierungVerwaltungsvorschrift(ldmlDocument, sliData.aktivzitierungenAdm());
      mapAktivzitierungRechtsprechung(ldmlDocument, sliData.aktivzitierungenRechtsprechung());
      mapAktivzitierungUli(ldmlDocument, sliData.aktivzitierungenUli());
    }
  }

  private void mapDocumentNumber(LdmlDocument ldmlDocument, String documentNumber) {
    ldmlDocument
      .frbrWork()
      .appendElementAndGet("akn:FRBRalias")
      .addAttribute("name", "Dokumentnummer")
      .addAttribute(VALUE, documentNumber);
  }

  private void mapDokumentart(LdmlDocument ldmlDocument, LiteratureDocumentCategory category) {
    DocumentCategory documentCategory = (category == LiteratureDocumentCategory.SLI)
      ? DocumentCategory.LITERATUR_SELBSTAENDIG
      : DocumentCategory.LITERATUR_UNSELBSTAENDIG;
    ldmlDocument
      .frbrWork()
      .appendElementAndGet("akn:FRBRsubtype")
      .addAttribute(VALUE, documentCategory.getPrefix());
  }

  private void mapVeroeffentlichungsJahre(
    LdmlDocument ldmlDocument,
    String veroeffentlichungsjahr
  ) {
    ldmlDocument
      .addProprietary()
      .appendElementAndGet("ris:meta")
      .appendElementAndGet("ris:veroeffentlichungsJahre")
      .appendElementAndGet("ris:veroeffentlichungsJahr")
      .appendText(StringUtils.defaultString(veroeffentlichungsjahr));
  }

  private void mapTitles(LdmlDocument ldmlDocument, LiteratureDocumentationUnitContent data) {
    // 1. mapHauptsachtitel
    if (StringUtils.isNotBlank(data.hauptsachtitel())) {
      mapToFBRBWorkAlias(data.hauptsachtitel(), ldmlDocument, "haupttitel");
      mapToLongTitle(data.hauptsachtitel(), ldmlDocument);
    } else {
      mapToLongTitle("", ldmlDocument);
    }

    // 2. mapTitelzusatz
    if (StringUtils.isNotBlank(data.hauptsachtitelZusatz())) {
      mapToFBRBWorkAlias(data.hauptsachtitelZusatz(), ldmlDocument, "haupttitelZusatz");
    }

    // 3. mapFingtitel
    if (StringUtils.isNotBlank(data.dokumentarischerTitel())) {
      mapToFBRBWorkAlias(data.dokumentarischerTitel(), ldmlDocument, "dokumentarischerTitel");
    }
  }

  private void mapClassifications(LdmlDocument ldmlDocument, List<DocumentType> dokumentTypen) {
    if (dokumentTypen != null && !dokumentTypen.isEmpty()) {
      List<String> values = dokumentTypen.stream().map(DocumentType::abbreviation).toList();
      mapToClassification(ldmlDocument, "doktyp", values);
    }
  }

  private void mapNote(LdmlDocument ldmlDocument, String noteText) {
    if (StringUtils.isBlank(noteText)) {
      return;
    }

    LdmlElement metaElement = new LdmlElement(ldmlDocument.getMeta());
    metaElement
      .appendElementAndGet("akn:notes")
      .addAttribute(SOURCE, "gesamtfussnoten")
      .appendElementAndGet("akn:note")
      .appendElementAndGet("akn:block")
      .addAttribute("name", "gesamtfussnote")
      .appendText(noteText.strip());
  }

  private void mapKurzreferat(LdmlDocument ldmlDocument) {
    ldmlDocument
      .mainBody()
      .appendElementAndGet("akn:hcontainer")
      .addAttribute("name", "crossheading");
  }

  private void mapToClassification(
    LdmlDocument ldmlDocument,
    String sourceAttributeValue,
    List<String> values
  ) {
    if (values == null || values.isEmpty()) {
      return;
    }
    LdmlElement classificationElement = ldmlDocument.addClassification(sourceAttributeValue);
    for (String value : values) {
      if (StringUtils.isNotBlank(value)) {
        mapToKeyword(classificationElement, value);
      }
    }
  }

  private void mapToKeyword(LdmlElement classificationElement, String value) {
    classificationElement
      .appendElementAndGet("akn:keyword")
      .addAttribute(SHOW_AS, value)
      .addAttribute(VALUE, value)
      .addAttribute("dictionary", UNDEFINED);
  }

  private void mapToLongTitle(String value, LdmlDocument ldmlDocument) {
    ldmlDocument
      .preface()
      .appendElementAndGet("akn:longTitle")
      .appendElementAndGet("akn:block")
      .addAttribute("name", "longTitle")
      .appendText(value);
  }

  private void mapToFBRBWorkAlias(String value, LdmlDocument ldmlDocument, String name) {
    ldmlDocument
      .frbrWork()
      .appendElementAndGet("akn:FRBRalias")
      .addAttribute("name", name)
      .addAttribute(VALUE, value);
  }

  private void mapAktivzitierungSelbstaendigeLiteratur(
    LdmlDocument ldmlDocument,
    List<AktivzitierungSli> activeSliReferences
  ) {
    if (activeSliReferences != null && !activeSliReferences.isEmpty()) {
      LdmlElement otherReferencesElement = ldmlDocument
        .addAnalysis()
        .prepareElement(OTHER_REFERENCES)
        .addAttribute(SOURCE, ACTIVE)
        .appendOnce();

      for (AktivzitierungSli reference : activeSliReferences) {
        String documentNumber = reference.documentNumber();
        String veroeffentlichungsJahr = reference.veroeffentlichungsJahr();
        String titel = reference.titel();
        String isbn = reference.isbn();

        String verfasser = reference.verfasser() != null
          ? String.join(", ", reference.verfasser())
          : "";

        String dokumentTypen = reference.dokumenttypen() != null
          ? reference
            .dokumenttypen()
            .stream()
            .map(DocumentType::abbreviation)
            .collect(Collectors.joining(", "))
          : "";

        // Currently the same style as the migration. Needs to be re-iterated and either here or in the migration project
        // adapted once the format of selbstaendigeLiteraturReference is final
        String showAsValue = Stream.of(titel, veroeffentlichungsJahr, verfasser)
          .filter(StringUtils::isNotBlank)
          .collect(Collectors.joining(", "));

        otherReferencesElement
          .appendElementAndGet(IMPLICIT_REFERENCE)
          .addAttribute(SHOW_AS, showAsValue)
          .appendElementAndGet("ris:selbstaendigeLiteraturReference")
          .addAttribute(DOCUMENT_NUMBER, documentNumber)
          .addAttribute("veroeffentlichungsJahr", veroeffentlichungsJahr)
          .addAttribute("buchtitel", titel)
          .addAttribute("isbn", isbn)
          .addAttribute("autor", verfasser)
          .addAttribute("dokumentTyp", dokumentTypen);
      }
    }
  }

  private void mapAktivzitierungVerwaltungsvorschrift(
    LdmlDocument ldmlDocument,
    List<AktivzitierungAdm> activeVvReferences
  ) {
    if (activeVvReferences != null && !activeVvReferences.isEmpty()) {
      LdmlElement otherReferences = ldmlDocument
        .addAnalysis()
        .prepareElement(OTHER_REFERENCES)
        .addAttribute(SOURCE, ACTIVE)
        .appendOnce();

      for (AktivzitierungAdm vvNode : activeVvReferences) {
        String abbreviation = vvNode.citationType();
        String reference = vvNode.aktenzeichen();
        String documentNumber = vvNode.documentNumber();
        String periodikum = vvNode.periodikum();
        String zitatstelle = vvNode.zitatstelle();
        String dokumenttyp = vvNode.dokumenttyp();
        String normgeber = vvNode.normgeber();
        String inkrafttretedatum = vvNode.inkrafttretedatum();

        String showAs = abbreviation + ", " + reference;

        LdmlElement risVvReference = otherReferences
          .appendElementAndGet(IMPLICIT_REFERENCE)
          .addAttribute(SHOW_AS, showAs)
          .appendElementAndGet("ris:verwaltungsvorschriftReference")
          .addAttribute("abbreviation", abbreviation) // citationType
          .addAttribute("reference", reference) // aktenzeichen
          .addAttribute(DOCUMENT_NUMBER, documentNumber)
          .addAttribute(DOKUMENT_TYP, dokumenttyp)
          .addAttribute("normgeber", normgeber)
          .addAttribute("inkrafttretedatum", inkrafttretedatum);

        if (StringUtils.isNoneBlank(periodikum, zitatstelle)) {
          risVvReference
            .appendElementAndGet("ris:fundstelle")
            .addAttribute("periodikum", periodikum)
            .addAttribute("zitatstelle", zitatstelle);
        }
      }
    }
  }

  private void mapAktivzitierungRechtsprechung(
    LdmlDocument ldmlDocument,
    List<AktivzitierungRechtsprechung> activeRechtsprechungReferences
  ) {
    if (activeRechtsprechungReferences != null && !activeRechtsprechungReferences.isEmpty()) {
      LdmlElement otherReferences = ldmlDocument
        .addAnalysis()
        .prepareElement(OTHER_REFERENCES)
        .addAttribute(SOURCE, ACTIVE)
        .appendOnce();

      for (AktivzitierungRechtsprechung caselawRefNode : activeRechtsprechungReferences) {
        String documentNumber = caselawRefNode.documentNumber();
        String citationType = caselawRefNode.citationType();
        String entscheidungsdatum = caselawRefNode.entscheidungsdatum();
        String aktenzeichen = caselawRefNode.aktenzeichen();
        String dokumenttyp = caselawRefNode.dokumenttyp();
        String gerichttyp = caselawRefNode.gerichttyp();
        String gerichtort = caselawRefNode.gerichtort();

        String showAs = Stream.of(
          citationType,
          gerichttyp,
          gerichtort,
          entscheidungsdatum,
          aktenzeichen
        )
          .filter(s -> s != null && !s.isBlank())
          .collect(Collectors.joining(", "));

        otherReferences
          .appendElementAndGet(IMPLICIT_REFERENCE)
          .addAttribute(SHOW_AS, showAs)
          .appendElementAndGet("ris:caselawReference")
          .addAttribute("abbreviation", citationType)
          .addAttribute("court", gerichttyp)
          .addAttribute("courtLocation", gerichtort)
          .addAttribute("date", entscheidungsdatum)
          .addAttribute(DOCUMENT_NUMBER, documentNumber)
          .addAttribute(DOKUMENT_TYP, dokumenttyp)
          .addAttribute("referenceNumber", aktenzeichen);
      }
    }
  }

  private void mapAktivzitierungUli(
    LdmlDocument ldmlDocument,
    List<AktivzitierungUli> activeUliReferences
  ) {
    if (activeUliReferences != null && !activeUliReferences.isEmpty()) {
      LdmlElement otherReferences = ldmlDocument
        .addAnalysis()
        .prepareElement(OTHER_REFERENCES)
        .addAttribute(SOURCE, ACTIVE)
        .appendOnce();

      for (AktivzitierungUli uliRefNode : activeUliReferences) {
        String documentNumber = uliRefNode.documentNumber();
        String periodikum = uliRefNode.periodikum();
        String zitatstelle = uliRefNode.zitatstelle();

        String verfasser = uliRefNode.verfasser() != null
          ? String.join(", ", uliRefNode.verfasser())
          : "";

        String dokumentTypen = uliRefNode.dokumenttypen() != null
          ? uliRefNode
            .dokumenttypen()
            .stream()
            .map(DocumentType::abbreviation)
            .collect(Collectors.joining(", "))
          : "";

        String showAs = Stream.of(periodikum, zitatstelle, verfasser)
          .filter(s -> s != null && !s.isBlank())
          .collect(Collectors.joining(", "));

        otherReferences
          .appendElementAndGet(IMPLICIT_REFERENCE)
          .addAttribute(SHOW_AS, showAs)
          .appendElementAndGet("ris:unselbstaendigeLiteraturReference")
          .addAttribute(DOCUMENT_NUMBER, documentNumber)
          .addAttribute(DOKUMENT_TYP, dokumentTypen)
          .addAttribute("periodikum", periodikum)
          .addAttribute("verfasser", verfasser)
          .addAttribute("zitatstelle", zitatstelle);
      }
    }
  }
}
