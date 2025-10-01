package de.bund.digitalservice.ris.adm_vwv.application;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentType;
import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentationOffice;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class UserInformationService {

  public record UserDocumentDetails(DocumentationOffice office, DocumentType type) {}

  public UserDocumentDetails getCurrentUserDocumentDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof JwtAuthenticationToken token)) {
      throw new IllegalStateException("User is not authenticated with a JWT token.");
    }

    @SuppressWarnings("unchecked")
    List<String> userRoles = (List<String>) token
      .getToken()
      .getClaimAsMap("realm_access")
      .getOrDefault("roles", Collections.emptyList());

    if (CollectionUtils.isEmpty(userRoles)) {
      log.error("User '{}' has no roles in the 'realm_access' claim.", token.getName());
      throw new IllegalStateException("User has no assigned roles.");
    }

    ApplicationRole applicationRole = userRoles
      .stream()
      .map(roleName -> {
        // Attempt role conversion; return null on failure.
        try {
          return ApplicationRole.from(roleName);
        } catch (IllegalArgumentException _) {
          return null;
        }
      })
      // Remove illegal roles
      .filter(java.util.Objects::nonNull)
      // We assume the user has only one valid role
      .findFirst()
      .orElseThrow(() -> {
        log.error(
          "User '{}' does not have a required application role. Found roles: {}",
          token.getName(),
          userRoles
        );
        return new IllegalStateException("User does not have a required application role.");
      });

    DocumentType type = applicationRole.getDocumentType();
    DocumentationOffice office = applicationRole.getDocumentationOffice(token.getToken());

    return new UserDocumentDetails(office, type);
  }
}
