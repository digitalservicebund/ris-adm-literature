package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.IDocumentationContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.util.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import java.util.List;
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
public class LiteratureLdmlConverterStrategy implements LdmlConverterStrategy {

  private final MinimalLdmlDocument minimalLdmlDocument = new MinimalLdmlDocument();

  private static final String VALUE = "value";
  private static final String SOURCE = "source";
  private static final String UNDEFINED = "attributsemantik-noch-undefiniert";

  private record LiteratureData(
    LiteratureDocumentCategory category,
    String subType,
    String documentNumber,
    String veroeffentlichungsjahr,
    String note,
    List<DocumentType> dokumentTypen,
    String haupttitel,
    String zusatz,
    String dokumentarischerTitel
  ) {
    // We should re-assess if we really need to different UnitContents for Uli and SLi once we have all attributes
    static LiteratureData from(IDocumentationContent content) {
      return switch (content) {
        case UliDocumentationUnitContent c -> new LiteratureData(
          LiteratureDocumentCategory.ULI,
          "LU",
          c.documentNumber(),
          c.veroeffentlichungsjahr(),
          c.note(),
          c.dokumenttypen(),
          c.hauptsachtitel(),
          c.hauptsachtitelZusatz(),
          c.dokumentarischerTitel()
        );
        case SliDocumentationUnitContent c -> new LiteratureData(
          LiteratureDocumentCategory.SLI,
          "LS",
          c.documentNumber(),
          c.veroeffentlichungsjahr(),
          c.note(),
          c.dokumenttypen(),
          c.hauptsachtitel(),
          c.hauptsachtitelZusatz(),
          c.dokumentarischerTitel()
        );
        default -> throw new IllegalStateException(
          "Unexpected content type: " + content.getClass()
        );
      };
    }
  }

  @Override
  public boolean supports(IDocumentationContent content) {
    return (
      content instanceof UliDocumentationUnitContent ||
      content instanceof SliDocumentationUnitContent
    );
  }

  @Override
  public String convertToLdml(@Nonnull IDocumentationContent content, String previousXmlVersion) {
    try {
      if (previousXmlVersion != null) {
        // We'd need an LdmlDocument parser here if we want to edit
        // For now, just create a new one
        log.warn("Editing previous XML version is not fully implemented; creating new document.");
      }

      LiteratureData data = LiteratureData.from(content);

      LdmlDocument ldmlDocument = minimalLdmlDocument.create(data.category());

      transformToLdml(ldmlDocument, data);

      return LiteratureXmlWriter.xmlToString(ldmlDocument.getDocument());
    } catch (Exception e) {
      log.error("Failed to convert Literature content to LDML", e);
      throw new PublishingFailedException("Failed to convert Literature content to LDML", e);
    }
  }

  // Order should be the same as in migration project
  private void transformToLdml(LdmlDocument ldmlDocument, LiteratureData data) {
    mapDocumentNumber(ldmlDocument, data.documentNumber());
    mapDokumentart(ldmlDocument, data.subType());
    mapVeroeffentlichungsJahre(ldmlDocument, data.veroeffentlichungsjahr());
    mapTitles(ldmlDocument, data);
    mapClassifications(ldmlDocument, data.dokumentTypen());
    mapNote(ldmlDocument, data.note());
    mapKurzreferat(ldmlDocument);
  }

  private void mapDocumentNumber(LdmlDocument ldmlDocument, String documentNumber) {
    ldmlDocument
      .frbrWork()
      .appendElementAndGet("akn:FRBRalias")
      .addAttribute("name", "Dokumentnummer")
      .addAttribute(VALUE, documentNumber);
  }

  private void mapDokumentart(LdmlDocument ldmlDocument, String subType) {
    ldmlDocument.frbrWork().appendElementAndGet("akn:FRBRsubtype").addAttribute(VALUE, subType);
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

  private void mapTitles(LdmlDocument ldmlDocument, LiteratureData data) {
    // 1. mapHauptsachtitel
    if (StringUtils.isNotBlank(data.haupttitel())) {
      mapToFBRBWorkAlias(data.haupttitel(), ldmlDocument, "haupttitel");
      mapToLongTitle(data.haupttitel(), ldmlDocument);
    } else {
      mapToLongTitle("", ldmlDocument);
    }

    // 2. mapTitelzusatz
    if (StringUtils.isNotBlank(data.zusatz())) {
      mapToFBRBWorkAlias(data.zusatz(), ldmlDocument, "haupttitelZusatz");
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
      .addAttribute("showAs", value)
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
}
