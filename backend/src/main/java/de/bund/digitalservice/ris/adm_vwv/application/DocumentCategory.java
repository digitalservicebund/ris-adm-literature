package de.bund.digitalservice.ris.adm_vwv.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents categories of documents managed by the application,
 * each with a unique string prefix used for identification in document numbers.
 * In German known as 'Dokumentart'.
 */
@RequiredArgsConstructor
@Getter
public enum DocumentCategory {
  LITERATUR_SELBSTSTAENDIG("LS"),
  LITERATUR_UNSELBSTSTAENDIG("LU"),
  VERWALTUNGSVORSCHRIFTEN("NR");

  private final String prefix;
}
