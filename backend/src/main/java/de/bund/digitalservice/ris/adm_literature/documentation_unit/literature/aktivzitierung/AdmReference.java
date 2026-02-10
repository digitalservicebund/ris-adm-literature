package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public record AdmReference(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String inkrafttretedatum,
  String langueberschrift,
  String dokumenttyp,
  List<String> normgeberList,
  List<String> fundstellen,
  List<String> aktenzeichenList,
  List<String> zitierdaten
) {}
