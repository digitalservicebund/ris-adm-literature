package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;

public record AktivzitierungQuery(
  String documentNumber,
  String periodikum,
  String zitatstelle,
  String inkrafttretedatum,
  String aktenzeichen,
  String dokumenttyp,
  String normgeber,
  QueryOptions queryOptions
) {}
