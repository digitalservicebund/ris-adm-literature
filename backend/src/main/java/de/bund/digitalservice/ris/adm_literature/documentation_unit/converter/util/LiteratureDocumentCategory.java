package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration for literature document categories.
 */
@RequiredArgsConstructor
public enum LiteratureDocumentCategory {
  ULI("http://ldml.neuris.de/literature/unselbstaendig/meta/"),
  SLI("http://ldml.neuris.de/literature/selbstaendig/meta/");

  @Getter
  private final String namespace;
}
