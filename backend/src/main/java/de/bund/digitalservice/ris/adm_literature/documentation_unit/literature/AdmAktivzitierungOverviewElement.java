package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import jakarta.annotation.Nonnull;
import java.util.UUID;

public record AdmAktivzitierungOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String inkrafttretedatum,
  String langueberschrift,
  String dokumenttyp,
  String normgeber,
  String periodikum,
  String zitatstelle,
  String aktenzeichen
) {}
