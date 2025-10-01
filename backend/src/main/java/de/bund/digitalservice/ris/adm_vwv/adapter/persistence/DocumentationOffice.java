package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
//TODO: Decide how to implement this after we have the roles in bareId
public enum DocumentationOffice {
  // Existing type
  ADM_VWV("KSNR", "adm_vwv_user"),

  // New types
  BAG("KALU", "ROLE_BAG"),
  BFH("STLU", "ROLE_BFH"),
  BSG("KSLU", "ROLE_BSG"),
  BVERFG("KVLU", "ROLE_BVERFG"),
  BVERWG("WBLU", "ROLE_BVERWG");

  /**
   * The prefix used for the document number (e.g., "KSNR", "KALU").
   */
  private final String prefix;

  /**
   * The security role associated with this document type.
   */
  private final String role;
}
