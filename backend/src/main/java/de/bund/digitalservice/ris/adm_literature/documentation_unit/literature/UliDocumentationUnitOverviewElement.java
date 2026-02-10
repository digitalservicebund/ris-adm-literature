package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Represents an overview element for a ULI documentation unit.
 * This record encapsulates essential details related to a documentation unit,
 * including its unique identifier, document number, publication year, title,
 * document types, and authors.
 *
 * @param id                 The unique identifier of the documentation unit.
 * @param documentNumber     The public document number associated with the unit.
 * @param fundstellen        The list of Fundstellen
 * @param dokumenttypen      A list of {@link DocumentType} objects representing the document types.
 * @param verfasser          A list of strings representing the authors.
 */
public record UliDocumentationUnitOverviewElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  List<String> fundstellen,
  List<String> dokumenttypen,
  List<String> verfasser
) {}
