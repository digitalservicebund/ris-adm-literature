package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;

/**
 * Test page class for creating instances of {@link Page} for testing purposes.
 */
public class TestPage {

  public static <T> Page<T> create(List<T> content) {
    return new Page<>(
      content,
      content.size(),
      0,
      content.size(),
      content.size(),
      true,
      true,
      content.isEmpty()
    );
  }
}
