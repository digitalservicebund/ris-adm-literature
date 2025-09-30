package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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

  public static DocumentationOffice fromRole(String userRole) {
    String roleName = userRole.startsWith("ROLE_") ? userRole.substring(5) : userRole;

    return Arrays.stream(values())
      .filter(
        type -> type.getRole().equalsIgnoreCase(roleName) || type.name().equalsIgnoreCase(roleName)
      )
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + userRole));
  }
}
