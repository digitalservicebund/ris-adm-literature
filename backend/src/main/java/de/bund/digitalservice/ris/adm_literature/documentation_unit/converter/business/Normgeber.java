package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.Institution;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.institution.InstitutionType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.Region;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Normgeber business record.
 *
 * @param id The uuid
 * @param institution The institution
 * @param regions The list of regions
 */
public record Normgeber(
  @Nonnull UUID id,
  @Nonnull Institution institution,
  @Nonnull List<Region> regions
) {
  /**
   * Returns a string representation of this instance containing the institution name
   * and region in case if the institution is of type {@code INSTITUTION}.
   *
   * @return String representation of this instance
   */
  public String format() {
    String formatted = institution.name();
    if (institution.type() == InstitutionType.INSTITUTION) {
      formatted = regions().getFirst().code() + " " + institution.name();
    }
    return formatted;
  }
}
