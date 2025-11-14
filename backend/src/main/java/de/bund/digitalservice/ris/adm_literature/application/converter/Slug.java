package de.bund.digitalservice.ris.adm_literature.application.converter;

import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * Class for normalizing a string into a readable slug to use in URIs or URLs.
 */
@RequiredArgsConstructor
class Slug {

  private static final Map<String, String> GERMAN_LETTER_REPLACEMENTS = Map.ofEntries(
    Map.entry("ä", "ae"),
    Map.entry("ö", "oe"),
    Map.entry("ü", "ue"),
    Map.entry("ß", "ss")
  );

  private final String value;

  @Override
  public String toString() {
    var result = value.toLowerCase();
    for (Map.Entry<
      String,
      String
    > germanLetterReplacement : GERMAN_LETTER_REPLACEMENTS.entrySet()) {
      result = result.replace(germanLetterReplacement.getKey(), germanLetterReplacement.getValue());
    }
    return result
      // Remove all no-ASCII characters
      .replaceAll("[^\\p{ASCII}]+", "")
      // Replace spaces and non-word characters with hyphen
      .replaceAll("\\W+", "-")
      // Remove leading and trailing hyphens
      .replaceAll("(^-)|(-$)", "");
  }
}
