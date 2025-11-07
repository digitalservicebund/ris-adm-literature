package de.bund.digitalservice.ris.adm_vwv.application.converter.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentType {
  ULI("http://ldml.neuris.de/literature/unselbstaendig/meta/"),
  SLI("http://ldml.neuris.de/literature/selbstaendig/meta/");

  @Getter
  private final String namespace;
}
