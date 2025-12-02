package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.List;

/**
 * Test class for easily creating instances of {@link UliDocumentationUnitContent}.
 */
public class TestDocumentationUnitContent {

  public static UliDocumentationUnitContent createUli(
    String documentNumber,
    String veroeffentlichungsjahr
  ) {
    return new UliDocumentationUnitContent(
      null,
      documentNumber,
      veroeffentlichungsjahr,
      List.of(new DocumentType("Auf", "Aufsatz")),
      null,
      null,
      null,
      null
    );
  }

  public static SliDocumentationUnitContent createSli(
    String documentNumber,
    String veroeffentlichungsjahr
  ) {
    return new SliDocumentationUnitContent(
      null,
      documentNumber,
      veroeffentlichungsjahr,
      List.of(new DocumentType("Auf", "Aufsatz")),
      null,
      null,
      null,
      null,
      List.of(
        new SliDocumentationUnitContent.AktivzitierungSli(
          "docnum",
          "11",
          "titel",
          "isbn",
          "autor",
          new DocumentType("VR", "Verwaltungsregelung")
        )
      )
    );
  }
}
