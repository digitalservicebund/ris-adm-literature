package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.Institution;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.InstitutionType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.Region;
import java.util.List;
import java.util.UUID;

public class TestNormgeber {

  public static Normgeber createByLegalEntity(String legalEntityName) {
    return new Normgeber(
      UUID.randomUUID(),
      new Institution(
        UUID.randomUUID(),
        legalEntityName,
        null,
        InstitutionType.LEGAL_ENTITY,
        List.of()
      ),
      List.of()
    );
  }

  public static Normgeber createByInstitution(String institutionName, String regionCode) {
    return new Normgeber(
      UUID.randomUUID(),
      new Institution(
        UUID.randomUUID(),
        institutionName,
        null,
        InstitutionType.INSTITUTION,
        List.of()
      ),
      List.of(new Region(UUID.randomUUID(), regionCode, null))
    );
  }
}
