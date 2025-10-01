package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import lombok.Getter;

@Getter
public enum DocumentationOffice {
  BAG("KA"),
  BFH("ST"),
  BSG("KS"),
  BVERFG("KV"),
  BVERWG("WB");

  public final String prefix;

  DocumentationOffice(String prefix) {
    this.prefix = prefix;
  }
}
