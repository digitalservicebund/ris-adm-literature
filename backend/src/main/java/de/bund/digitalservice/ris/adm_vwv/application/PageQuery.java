package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Sort;

/**
 * Query object
 *
 * @param page Page (of pagination) to return
 * @param size Size of page
 * @param sortBy Property to sort by
 * @param sortDirection Direction to sort by
 * @param paged {@code true} if result should be paginated, {@code false} otherwise
 */
public record PageQuery(
  int page,
  int size,
  @Nonnull String sortBy,
  @Nonnull Sort.Direction sortDirection,
  boolean paged
) {}
