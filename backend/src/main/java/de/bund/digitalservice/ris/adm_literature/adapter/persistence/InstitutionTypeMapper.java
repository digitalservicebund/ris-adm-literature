package de.bund.digitalservice.ris.adm_literature.adapter.persistence;

import de.bund.digitalservice.ris.adm_literature.application.InstitutionType;
import jakarta.annotation.Nonnull;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * Utility class for mapping instances of {@link de.bund.digitalservice.ris.adm_literature.application.InstitutionType} to {@code String} and vice versa.
 */
@UtilityClass
class InstitutionTypeMapper {

  private static final BidiMap<String, InstitutionType> MAPPING = new DualHashBidiMap<>(
    Map.of("organ", InstitutionType.INSTITUTION, "jurpn", InstitutionType.LEGAL_ENTITY)
  );

  String mapInstitutionType(@Nonnull InstitutionType institutionType) {
    return MAPPING.inverseBidiMap().get(institutionType);
  }

  InstitutionType mapInstitutionTypeString(@Nonnull String type) {
    return MAPPING.get(type);
  }
}
