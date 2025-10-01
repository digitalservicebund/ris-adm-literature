package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

public enum DocumentType {
  LITERATURE("LU"),
  ADMINISTRATIVE("NR");

  public final String prefix;

  DocumentType(String prefix) {
    this.prefix = prefix;
  }
}
