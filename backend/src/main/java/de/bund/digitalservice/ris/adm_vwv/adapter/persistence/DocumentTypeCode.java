package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

public enum DocumentTypeCode {
  LITERATURE_DEPENDENT("LU"),
  ADMINISTRATIVE("NR");

  public final String prefix;

  DocumentTypeCode(String prefix) {
    this.prefix = prefix;
  }
}
