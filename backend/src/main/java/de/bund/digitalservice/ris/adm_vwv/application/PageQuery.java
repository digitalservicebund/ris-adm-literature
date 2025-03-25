package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Sort;

/**
 * Query object
 *
 * @param paged {@code true} if result should be paginated, {@code false} otherwise
 * @param page Page (of pagination) to return
 * @param size Size of page
 * @param sortBy Property to sort by
 * @param sortDirection Direction to sort by
 */
public record PageQuery(
  int page,
  int size,
  @Nonnull String sortBy,
  @Nonnull Sort.Direction sortDirection,
  boolean paged
) {}
