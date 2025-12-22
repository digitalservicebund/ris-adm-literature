package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with adm Aktivzitierung results.
 *
 * @param documentationUnits         List of documentation units' data
 * @param page                       Pagination data
 */
public record AdmAktivzitierungResults(
  List<AdmAktivzitierungResult> documentationUnits,
  PageResponse page
) {}
