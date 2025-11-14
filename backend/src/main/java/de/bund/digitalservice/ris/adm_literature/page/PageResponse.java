package de.bund.digitalservice.ris.adm_literature.page;

import jakarta.annotation.Nonnull;

/**
 * Page response model. Only contains pagination data, not the page content itself.
 * @param size The requested page size
 * @param number The number of this page, beginning with 0
 * @param numberOfElements The actual number of elements in this page ({@code numberOfElements} must be less than equal
 *                         {@code size})
 * @param totalElements The total number of elements (unpaged)
 * @param first {@code true} if this is the very first page, otherwise {@code false}
 * @param last {@code true} if this is the very last page, otherwise {@code false}
 * @param empty {@code true} if this page is empty, which means that the page content is empty as well. Otherwise, set
 *                          to {@code false}
 */
public record PageResponse(
  int size,
  int number,
  int numberOfElements,
  long totalElements,
  boolean first,
  boolean last,
  boolean empty
) {
  public PageResponse(@Nonnull Page<?> page) {
    this(
      page.size(),
      page.number(),
      page.numberOfElements(),
      page.totalElements(),
      page.first(),
      page.last(),
      page.empty()
    );
  }
}
