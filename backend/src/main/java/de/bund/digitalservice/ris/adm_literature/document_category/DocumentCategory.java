package de.bund.digitalservice.ris.adm_literature.document_category;

import de.bund.digitalservice.ris.adm_literature.config.multischema.SchemaType;
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
  /**
   * The combined literatur value is needed for filtering citation types only.
   */
  LITERATUR(null, null, SchemaType.LITERATURE),
  LITERATUR_SELBSTAENDIG("LS", "publicLiteraturePublisher", SchemaType.LITERATURE),
  LITERATUR_UNSELBSTAENDIG("LU", "publicLiteraturePublisher", SchemaType.LITERATURE),
  VERWALTUNGSVORSCHRIFTEN("NR", "publicBsgPublisher", SchemaType.ADM);

  private final String prefix;
  private final String publisherName;
  private final SchemaType schemaType;
}
