package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * ULI documentation unit query.
 *
 * @param documentNumber     A string representing the document number to filter by.
 * @param periodikum         A string representing the periodical to filter by.
 * @param zitatstelle        A string representing the citation from the periodikum to filter by.
 * @param dokumenttypen      A list of {@link DocumentType} objects representing the document types to filter by.
 * @param verfasser          A list of strings representing the authors to filter by.
 * @param queryOptions       Page query options
 */
public record UliDocumentationUnitQuery(
  String documentNumber,
  String periodikum,
  String zitatstelle,
  List<String> dokumenttypen,
  List<String> verfasser,
  @Nonnull QueryOptions queryOptions
) {}
