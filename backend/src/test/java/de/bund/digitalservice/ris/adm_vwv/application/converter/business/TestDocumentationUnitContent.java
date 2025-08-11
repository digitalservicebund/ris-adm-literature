package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import java.util.List;

/**
 * Test class for easily creating instances of {@link DocumentationUnitContent}.
 */
public class TestDocumentationUnitContent {

  public static DocumentationUnitContent create(String documentNumber, String languberschrift) {
    return new DocumentationUnitContent(
      null,
      documentNumber,
      List.of(),
      List.of(),
      languberschrift,
      List.of(),
      List.of(),
      null,
      null,
      null,
      null,
      List.of(),
      true,
      null,
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of()
    );
  }
}
