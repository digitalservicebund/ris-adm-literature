package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Represents an overview element for a SLI documentation unit.
 * This record encapsulates essential details related to a documentation unit,
 * including its unique identifier, document number, publication year, title,
 * document types, and authors.
 *
 * @param id                 The unique identifier of the documentation unit.
 * @param documentNumber     The public document number associated with the unit.
 * @param veroeffentlichungsjahr The publication year of the documentation unit.
 * @param titel              The title of the documentation unit.
 * @param dokumenttypen      A list of document types that categorize the unit.
 * @param verfasser          A list of authors associated with the documentation unit.
 */
public record SliDocumentationUnitOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String veroeffentlichungsjahr,
  String titel,
  List<String> dokumenttypen,
  List<String> verfasser
) {}
