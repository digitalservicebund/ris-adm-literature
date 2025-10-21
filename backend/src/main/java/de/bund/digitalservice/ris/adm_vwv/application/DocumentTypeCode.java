package de.bund.digitalservice.ris.adm_vwv.application;

/**
 * Represents the types of documents managed by the application,
 * each with a unique string prefix used for identification.
 */
public enum DocumentTypeCode {
  LITERATUR_SELBSTSTAENDIG("LS"),
  LITERATUR_UNSELBSTSTAENDIG("LU"),
  VERWALTUNGSVORSCHRIFTEN("NR");

  public final String prefix;

  DocumentTypeCode(String prefix) {
    this.prefix = prefix;
  }
}
