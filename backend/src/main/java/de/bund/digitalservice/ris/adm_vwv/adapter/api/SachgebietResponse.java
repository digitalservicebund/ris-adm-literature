package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.Sachgebiet;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Field of law response wrapping a list of fields of law.
 *
 * @param sachgebiete Fields of law
 */
public record SachgebietResponse(@Nonnull List<Sachgebiet> sachgebiete) {}
