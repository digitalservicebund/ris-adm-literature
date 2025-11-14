package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.transform;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.Normgeber;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.AkomaNtoso;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.Proprietary;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisMeta;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.ldml.RisNormgeber;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.Institution;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.InstitutionService;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.InstitutionType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.Region;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.RegionService;
import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Transformer for 'Normgeber'.
 */
@RequiredArgsConstructor
@Component
public class NormgeberTransformer {

  private final InstitutionService institutionService;
  private final RegionService regionService;

  /**
   * Transforms the {@code AkomaNtoso} object to a list of Normgeber.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return Normgeber list, or an empty list if the surrounding {@code <proprietary>} element is {@code null}
   */
  public List<Normgeber> transform(@Nonnull AkomaNtoso akomaNtoso) {
    List<RisNormgeber> risNormgeberList = Optional.ofNullable(
      akomaNtoso.getDoc().getMeta().getProprietary()
    )
      .map(Proprietary::getMeta)
      .map(RisMeta::getNormgeber)
      .orElse(List.of());
    return risNormgeberList
      .stream()
      .map(risNormgeber -> {
        InstitutionType institutionType = InstitutionType.LEGAL_ENTITY;
        String institutionName = risNormgeber.getStaat();
        if (risNormgeber.getOrgan() != null) {
          institutionType = InstitutionType.INSTITUTION;
          institutionName = risNormgeber.getOrgan();
        }
        String summary = institutionName + " (" + institutionType + ")";
        Institution institution = institutionService
          .findInstitutionByNameAndType(institutionName, institutionType)
          .orElseThrow(() -> new IllegalArgumentException("Institution not found: " + summary));
        List<Region> regions = new ArrayList<>();
        if (risNormgeber.getOrgan() != null) {
          regionService.findRegionByCode(risNormgeber.getStaat()).ifPresent(regions::add);
        }
        return new Normgeber(UUID.randomUUID(), institution, regions);
      })
      .toList();
  }
}
