package de.bund.digitalservice.ris.adm_literature.document_category;

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
  LITERATUR_SELBSTAENDIG("LS", "publicLiteraturePublisher"),
  LITERATUR_UNSELBSTAENDIG("LU", "publicLiteraturePublisher"),
  VERWALTUNGSVORSCHRIFTEN("NR", "publicBsgPublisher");

  private final String prefix;

  private final String publisherName;
}
