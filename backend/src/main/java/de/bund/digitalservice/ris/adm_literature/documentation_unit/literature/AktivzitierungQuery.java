package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;

/**
 * Query parameters for searching administrative citations (Aktivzitierungen).
 *
 * @param documentNumber    Document number to search for
 * @param periodikum        Periodikum to search for
 * @param zitatstelle       Zitatstelle to search for
 * @param inkrafttretedatum Effective date to search for
 * @param aktenzeichen      Docket or reference number to search for
 * @param dokumenttyp       Document type to search for
 * @param normgeber         Issuing authority to search for
 * @param queryOptions      Pagination and sorting options
 */
public record AktivzitierungQuery(
  String documentNumber,
  String periodikum,
  String zitatstelle,
  String inkrafttretedatum,
  String aktenzeichen,
  String dokumenttyp,
  String normgeber,
  @Nonnull QueryOptions queryOptions
) {}
