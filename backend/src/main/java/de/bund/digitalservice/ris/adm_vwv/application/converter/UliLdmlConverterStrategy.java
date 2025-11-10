package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.application.PublishingFailedException;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.IDocumentationContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.util.*;
import de.bund.digitalservice.ris.adm_vwv.application.converter.util.LiteratureDocumentCategory;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * LDML publish converter service for transforming business models into XML/LDML keeping migrated content
 * not included in the business model like 'Verwaltungsdaten'.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UliLdmlConverterStrategy implements LdmlConverterStrategy {

  private final MinimalLdmlDocument minimalLdmlDocument = new MinimalLdmlDocument();

  private static final String VALUE = "value";
  private static final String SOURCE = "source";
  private static final String UNDEFINED = "attributsemantik-noch-undefiniert";

  @Override
  public boolean supports(IDocumentationContent content) {
    return content instanceof UliDocumentationUnitContent;
  }

  @Override
  public String convertToLdml(@Nonnull IDocumentationContent content, String previousXmlVersion) {
    UliDocumentationUnitContent uliContent = (UliDocumentationUnitContent) content;
    LdmlDocument ldmlDocument;

    try {
      if (previousXmlVersion != null) {
        // We'd need an LdmlDocument parser here if we want to edit
        // For now, just create a new one
        log.warn(
          "Editing previous XML version is not fully implemented in UliLdmlConverterStrategy; creating new document."
        );
      }
      // We create a default ULI doc type.
      ldmlDocument = minimalLdmlDocument.create(LiteratureDocumentCategory.ULI);

      // === Populate structure based on XmlItemProcessor logic ===

      mapDocumentNumber(ldmlDocument, uliContent.documentNumber());
      mapDokumentart(ldmlDocument, uliContent.dokumentTyp());
      mapVeroeffentlichungsJahre(ldmlDocument, uliContent.veroeffentlichungsjahr());
      mapTitles(ldmlDocument, uliContent);
      mapClassifications(ldmlDocument, uliContent.dokumentTyp());
      mapNote(ldmlDocument, uliContent.note());

      return LiteratureXmlWriter.xmlToString(ldmlDocument.getDocument());
    } catch (Exception e) {
      log.error("Failed to convert ULI content to LDML", e);
      throw new PublishingFailedException("Failed to convert ULI content to LDML", e);
    }
  }

  private void mapClassifications(LdmlDocument ldmlDocument, List<DocumentType> dokumentTypen) {
    if (dokumentTypen != null && !dokumentTypen.isEmpty()) {
      List<String> values = dokumentTypen.stream().map(DocumentType::abbreviation).toList();
      mapToClassification(ldmlDocument, "doktyp", values);
    }
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
      .addAttribute("showAs", value)
      .addAttribute(VALUE, value)
      .addAttribute("dictionary", UNDEFINED);
  }

  private void mapDocumentNumber(LdmlDocument ldmlDocument, String documentNumber) {
    ldmlDocument
      .frbrWork()
      .appendElementAndGet("akn:FRBRalias")
      .addAttribute("name", "Dokumentnummer")
      .addAttribute(VALUE, documentNumber);
  }

  private void mapDokumentart(LdmlDocument ldmlDocument, List<DocumentType> dokumentTypen) {
    if (dokumentTypen == null || dokumentTypen.isEmpty()) {
      return;
    }
    LdmlElement frbrWork = ldmlDocument.frbrWork();
    for (DocumentType typ : dokumentTypen) {
      frbrWork.appendElementAndGet("akn:FRBRsubtype").addAttribute(VALUE, typ.abbreviation());
    }
  }

  private void mapTitles(LdmlDocument ldmlDocument, UliDocumentationUnitContent uliContent) {
    // 1. mapHauptsachtitel
    if (StringUtils.isNotBlank(uliContent.hauptsachtitel())) {
      mapToLongTitle(uliContent.hauptsachtitel(), ldmlDocument);
      mapToFBRBWorkAlias(uliContent.hauptsachtitel(), ldmlDocument, "haupttitel");
    } else {
      mapToLongTitle("", ldmlDocument);
    }

    // 2. mapTitelzusatz
    if (StringUtils.isNotBlank(uliContent.hauptsachtitelZusatz())) {
      mapToFBRBWorkAlias(uliContent.hauptsachtitelZusatz(), ldmlDocument, "haupttitelZusatz");
    }

    // 3. mapFingtitel
    if (StringUtils.isNotBlank(uliContent.dokumentarischerTitel())) {
      mapToFBRBWorkAlias(uliContent.dokumentarischerTitel(), ldmlDocument, "dokumentarischerTitel");
    }
  }

  private void mapVeroeffentlichungsJahre(
    LdmlDocument ldmlDocument,
    String veroeffentlichungsjahr
  ) {
    LdmlElement risVeroeffentlichungsJahre = ldmlDocument
      .addProprietary()
      .appendElementAndGet("ris:meta")
      .appendElementAndGet("ris:veroeffentlichungsJahre");

    risVeroeffentlichungsJahre
      .appendElementAndGet("ris:veroeffentlichungsJahr")
      .appendText(StringUtils.defaultString(veroeffentlichungsjahr));
  }

  // This corresponds to mapGesamtfussnoten
  private void mapNote(LdmlDocument ldmlDocument, String noteText) {
    if (StringUtils.isBlank(noteText)) {
      return;
    }

    LdmlElement metaElement = new LdmlElement(ldmlDocument.getMeta());
    LdmlElement notesElement = metaElement
      .appendElementAndGet("akn:notes")
      .addAttribute(SOURCE, "gesamtfussnoten");

    notesElement
      .appendElementAndGet("akn:note")
      .appendElementAndGet("akn:block")
      .addAttribute("name", "gesamtfussnote")
      .appendText(noteText.strip());
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
}
