package de.bund.digitalservice.ris.adm_vwv.application;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentationOffice;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.CollectionUtils;

@Getter
@RequiredArgsConstructor
public enum ApplicationRole {
  ADMINISTRATIVE("adm_user", DocumentType.ADMINISTRATIVE) {
    @Override
    public DocumentationOffice getDocumentationOffice(Jwt jwt) {
      // For administrative users, the office is always BSG.
      return DocumentationOffice.BSG;
    }
  },

  LITERATURE("literature_user", DocumentType.LITERATURE) {
    @Override
    public DocumentationOffice getDocumentationOffice(Jwt jwt) {
      List<String> groups = jwt.getClaimAsStringList("groups");
      if (CollectionUtils.isEmpty(groups)) {
        throw new IllegalStateException(
          "User with role 'literature_user' is missing the required groups claim."
        );
      }
      String groupPath = groups.getFirst();
      String groupName = groupPath.substring(groupPath.lastIndexOf('/') + 1);
      return DocumentationOffice.valueOf(groupName.toUpperCase());
    }
  };

  private final String roleName;
  private final DocumentType documentType;

  /**
   * Determines the documentation office based on the specific logic for each role.
   * @param jwt The JWT token containing user claims.
   * @return The determined DocumentationOffice.
   */
  public abstract DocumentationOffice getDocumentationOffice(Jwt jwt);

  /**
   * Finds the corresponding ApplicationRole from a JWT role string.
   * @param roleName The role string from the token (e.g., "adm_user").
   * @return The matching ApplicationRole enum constant.
   * @throws IllegalArgumentException if no match is found.
   */
  public static ApplicationRole from(String roleName) {
    return Stream.of(values())
      .filter(role -> role.roleName.equalsIgnoreCase(roleName))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Unknown application role: " + roleName));
  }
}
