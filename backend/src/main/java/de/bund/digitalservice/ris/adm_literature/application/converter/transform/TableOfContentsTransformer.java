package de.bund.digitalservice.ris.adm_literature.application.converter.transform;

import de.bund.digitalservice.ris.adm_literature.application.converter.ldml.AkomaNtoso;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Transformer for 'Gliederung'.
 */
@RequiredArgsConstructor
public class TableOfContentsTransformer {

  private static final String TABLE_OF_CONTENT_FORMAT = "<p>%s</p>";

  /**
   * Transforms the {@code TableOfContents} object to a combined string with wrapping {@code }<p>}
   * elements for every line, so the resulting string is valid HTML.
   *
   * @param akomaNtoso The Akoma Ntoso XML object to transform
   * @return Combined string of table of content entries wrapped inside paragraph elements
   */
  public String transform(@Nonnull AkomaNtoso akomaNtoso) {
    List<String> tableOfContents = Optional.ofNullable(
      akomaNtoso.getDoc().getMeta().getProprietary()
    )
      .map(proprietary -> proprietary.getMeta().getTableOfContentsEntries())
      .orElse(List.of());
    return tableOfContents
      .stream()
      .map(TABLE_OF_CONTENT_FORMAT::formatted)
      .collect(Collectors.joining("\n"))
      .transform(StringUtils::trimToNull);
  }
}
