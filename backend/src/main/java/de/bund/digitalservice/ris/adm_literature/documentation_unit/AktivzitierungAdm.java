package de.bund.digitalservice.ris.adm_literature.documentation_unit;

public record AktivzitierungAdm(
  String documentNumber,
  String citationType,
  String periodikum,
  String zitatstelle,
  String inkrafttretedatum,
  String aktenzeichen,
  String dokumenttyp,
  String normgeber
) {}
