package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.List;

/**
 * Test class for easily creating instances of {@link AdmDocumentationUnitContent}.
 */
public class TestAdmDocumentationUnitContent {

  public static AdmDocumentationUnitContent create(String documentNumber, String langueberschrift) {
    return new AdmDocumentationUnitContent(
      null,
      documentNumber,
      List.of(),
      List.of(),
      langueberschrift,
      List.of(),
      List.of("2025-01-01"),
      null,
      null,
      null,
      null,
      List.of(),
      true,
      new DocumentType("VR", "Verwaltungsregelung"),
      null,
      List.of(),
      List.of(),
      List.of(),
      null,
      List.of(TestNormgeber.createByLegalEntity("Erste Jurpn")),
      List.of(),
      List.of(),
      List.of()
    );
  }
}
