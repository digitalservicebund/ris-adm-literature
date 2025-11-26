package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.IDocumentationContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.LiteratureDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.SliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.UliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.util.*;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
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
public class LiteratureLdmlConverterStrategy implements LdmlConverterStrategy {

  private final MinimalLdmlDocument minimalLdmlDocument = new MinimalLdmlDocument();

  private static final String VALUE = "value";
  private static final String SOURCE = "source";
  private static final String UNDEFINED = "attributsemantik-noch-undefiniert";
  private static final String ACTIVE = "active";
  private static final String SHOW_AS = "showAs";
  private static final String OTHER_REFERENCES = "akn:otherReferences";
  private static final String IMPLICIT_REFERENCE = "akn:implicitReference";

  @Override
  public boolean supports(IDocumentationContent content) {
    return (
      content instanceof UliDocumentationUnitContent ||
      content instanceof SliDocumentationUnitContent
    );
  }

  @Override
  public String convertToLdml(
    @Nonnull IDocumentationContent iDocumentationContent,
    String previousXmlVersion
  ) {
    try {
      if (previousXmlVersion != null) {
        // We'd need an LdmlDocument parser here if we want to edit
        // For now, just create a new one
        log.warn("Editing previous XML version is not fully implemented; creating new document.");
      }

      LiteratureDocumentCategory category =
        switch (iDocumentationContent) {
          case UliDocumentationUnitContent _ -> LiteratureDocumentCategory.ULI;
          case SliDocumentationUnitContent _ -> LiteratureDocumentCategory.SLI;
          default -> throw new IllegalStateException(
            "Unexpected content type: " + iDocumentationContent.getClass()
          );
        };

      LiteratureDocumentationUnitContent content =
        (LiteratureDocumentationUnitContent) iDocumentationContent;

      LdmlDocument ldmlDocument = minimalLdmlDocument.create(category);
      transformToLdml(ldmlDocument, content, category);

      return LiteratureXmlWriter.xmlToString(ldmlDocument.getDocument());
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
    if (data instanceof SliDocumentationUnitContent sliData) {
      mapAktivzitierungSelbstaendigeLiteratur(ldmlDocument, sliData.activeSliReferences());
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
    List<SliDocumentationUnitContent.ActiveSliReference> activeSliReferences
  ) {
    if (activeSliReferences != null && !activeSliReferences.isEmpty()) {
      LdmlElement otherReferencesElement = ldmlDocument
        .addAnalysis()
        .prepareElement(OTHER_REFERENCES)
        .addAttribute(SOURCE, ACTIVE)
        .appendOnce();

      for (SliDocumentationUnitContent.ActiveSliReference reference : activeSliReferences) {
        String documentNumber = reference.documentNumber();
        String veroeffentlichungsJahr = reference.veroeffentlichungsJahr();
        String title = reference.buchtitel();
        String isbn = reference.isbn();
        String autor = reference.autor();
        // urheber/ typ / verfasser need to be clarified

        String showAsValue = Stream.of(autor, documentNumber, isbn, title, veroeffentlichungsJahr)
          .filter(StringUtils::isNotBlank)
          .collect(Collectors.joining(", "));

        otherReferencesElement
          .appendElementAndGet(IMPLICIT_REFERENCE)
          .addAttribute(SHOW_AS, showAsValue)
          .appendElementAndGet("ris:selbstaendigeLiteraturReference")
          .addAttribute("documentNumber", documentNumber)
          .addAttribute("veroeffentlichungsJahr", veroeffentlichungsJahr)
          .addAttribute("buchtitel", title)
          .addAttribute("isbn", isbn)
          .addAttribute("autor", autor);
      }
    }
  }
}
