package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import java.util.List;

/**
 * Test class for easily creating instances of {@link UliDocumentationUnitContent}.
 */
public class TestUliDocumentationUnitContent {

  public static UliDocumentationUnitContent create(
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
}
