package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public record LiteratureDocumentationUnitOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String veroeffentlichungsjahr,
  String titel,
  List<String> dokumenttypen,
  List<String> verfasser
) {}
