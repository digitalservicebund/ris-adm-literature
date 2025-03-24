package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Sort;

/**
 * Details on pagination and sorting
 * 
 * @param paged Query with pagination?
 * @param page What page (of pagination) to return
 * @param size How many elements in a page (of pagination)
 * @param sortBy What column to sort by
 * @param sortDirection Sort ascending or descending?
 */
public record PageQuery(
  int page,
  int size,
  @Nonnull String sortBy,
  @Nonnull Sort.Direction sortDirection,
  boolean paged
) {}
