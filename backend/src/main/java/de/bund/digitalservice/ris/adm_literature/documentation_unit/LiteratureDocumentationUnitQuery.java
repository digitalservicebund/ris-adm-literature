package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Literature documentation unit query.
 *
 * @param documentNumber DocumentNumber to search for
 * @param veroeffentlichungsjahr Publication year to search for
 * @param dokumenttypen List of document types to filter by
 * @param titel Title to search for
 * @param verfasser List of authors to search for
 * @param queryOptions Page query options
 */
public record LiteratureDocumentationUnitQuery(
  String documentNumber,
  String veroeffentlichungsjahr,
  List<String> dokumenttypen,
  String titel,
  List<String> verfasser,
  @Nonnull QueryOptions queryOptions
) {}
