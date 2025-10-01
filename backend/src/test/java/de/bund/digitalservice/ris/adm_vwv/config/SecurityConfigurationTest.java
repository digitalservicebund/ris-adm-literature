package de.bund.digitalservice.ris.adm_vwv.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.bund.digitalservice.ris.adm_vwv.application.CustomJwtAuthenticationConverter;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class SecurityConfigurationTest {

  private Converter<Jwt, AbstractAuthenticationToken> authenticationConverter;

  private enum DocumentationOffice {
    BGH,
  }

  private enum DocumentType {
    VWV,
  }

  private enum ApplicationRole {
    ADM_VWV_USER(DocumentType.VWV, DocumentationOffice.BGH);

    private final DocumentType documentType;
    private final DocumentationOffice office;

    ApplicationRole(DocumentType type, DocumentationOffice office) {
      this.documentType = type;
      this.office = office;
    }

    public static ApplicationRole from(String roleName) {
      if ("adm_vwv_user".equalsIgnoreCase(roleName)) {
        return ADM_VWV_USER;
      }
      throw new IllegalArgumentException("Unknown role: " + roleName);
    }

    public DocumentType getDocumentType() {
      return documentType;
    }

    public DocumentationOffice getDocumentationOffice(Jwt jwt) {
      return office;
    }
  }

  // This is needed because the real record implements Serializable
  public record UserDocumentDetails(DocumentationOffice office, DocumentType type)
    implements Serializable {}

  @BeforeEach
  void setUp() {
    // We now test the new custom converter
    authenticationConverter = new CustomJwtAuthenticationConverter();
  }

  @Test
  void shouldConvertJwtToTokenWithCorrectPrincipalAndAuthorities() {
    final Map<String, Object> realmAccess = Map.of(
      "roles",
      List.of("adm_vwv_user", "another_role")
    );
    final Jwt jwt = createMockJwt(Map.of("realm_access", realmAccess));

    // when
    AbstractAuthenticationToken token = authenticationConverter.convert(jwt);

    // then
    assertThat(token).isNotNull();

    // 1. Verify the authorities are still created correctly
    assertThat(token.getAuthorities())
      .hasSize(2)
      .containsExactlyInAnyOrder(
        new SimpleGrantedAuthority("ROLE_adm_vwv_user"),
        new SimpleGrantedAuthority("ROLE_another_role")
      );

    // 2. Verify the principal is our custom UserDocumentDetails object
    assertThat(token.getPrincipal()).isInstanceOf(UserDocumentDetails.class);

    // 3. Verify the content of the principal is correct
    UserDocumentDetails details = (UserDocumentDetails) token.getPrincipal();
    assertThat(details.office()).isEqualTo(DocumentationOffice.BGH);
    assertThat(details.type()).isEqualTo(DocumentType.VWV);
  }

  @Test
  void shouldThrowExceptionWhenNoValidApplicationRoleIsFound() {
    // given: A JWT with roles that are not valid application roles
    final Map<String, Object> realmAccess = Map.of("roles", List.of("some_other_role", "guest"));
    final Jwt jwt = createMockJwt(Map.of("realm_access", realmAccess));

    // when / then
    assertThatThrownBy(() -> authenticationConverter.convert(jwt))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("User does not have a required application role.");
  }

  @Test
  void shouldThrowExceptionWhenRolesListIsEmpty() {
    // given: A JWT with an empty roles list
    final Map<String, Object> realmAccess = Map.of("roles", List.of());
    final Jwt jwt = createMockJwt(Map.of("realm_access", realmAccess));

    // when / then
    assertThatThrownBy(() -> authenticationConverter.convert(jwt))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("User does not have a required application role.");
  }

  /**
   * Helper method to create a mock JWT with specific claims.
   */
  private Jwt createMockJwt(Map<String, Object> customClaims) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", "test-subject");
    claims.putAll(customClaims);

    return new Jwt(
      "mock-token",
      Instant.now(),
      Instant.now().plusSeconds(60),
      Map.of("alg", "none"),
      claims
    );
  }
}
