package de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical;

import java.util.UUID;

public class TestLegalPeriodical {

  public static LegalPeriodical create(String abbreviation) {
    return new LegalPeriodical(UUID.randomUUID(), abbreviation, null, null, null, null);
  }
}
