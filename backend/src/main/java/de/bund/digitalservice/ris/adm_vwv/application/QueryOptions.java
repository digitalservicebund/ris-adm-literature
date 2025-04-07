package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Sort;

/**
 * Pagination details object
 *
 * @param pageNumber Page (of pagination) to return
 * @param pageSize Size of page
 * @param sortByProperty Property to sort by
 * @param sortDirection Direction to sort by
 * @param usePagination {@code true} if result should be paginated, {@code false} otherwise
 */
public record QueryOptions(
  int pageNumber,
  int pageSize,
  @Nonnull String sortByProperty,
  @Nonnull Sort.Direction sortDirection,
  boolean usePagination
) {}
