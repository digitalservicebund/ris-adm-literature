package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import de.bund.digitalservice.ris.adm_vwv.application.Institution;
import de.bund.digitalservice.ris.adm_vwv.application.InstitutionType;
import de.bund.digitalservice.ris.adm_vwv.application.Region;
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
