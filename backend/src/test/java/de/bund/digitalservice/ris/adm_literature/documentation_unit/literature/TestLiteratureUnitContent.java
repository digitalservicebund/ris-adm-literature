package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungAdm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungRechtsprechung;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungSli;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Test class for easily creating instances of {@link UliDocumentationUnitContent}
 * and {@link SliDocumentationUnitContent}.
 */
public class TestLiteratureUnitContent {

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
      "Hauptsachtitel",
      null,
      null,
      null,
      List.of(
        new AktivzitierungSli(
          UUID.randomUUID(),
          "docnum",
          "11",
          "titel",
          "isbn",
          List.of("autor"),
          List.of(new DocumentType("VR", "Verwaltungsregelung"))
        )
      ),
      List.of(
        new AktivzitierungAdm(
          UUID.randomUUID(),
          "KSNR999999990",
          "11",
          "periodikum",
          "zitstelle",
          "11.11.111",
          "akt",
          "doktyp",
          "normgeber"
        )
      ),
      List.of(
        new AktivzitierungRechtsprechung(
          UUID.randomUUID(),
          "KARE339410237",
          "Vergleiche",
          "1988-11-09",
          "5 Sa 292/88",
          "Vgl",
          "LarbG",
          "München"
        )
      ),
      Collections.emptyList()
    );
  }
}
