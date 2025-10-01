package de.bund.digitalservice.ris.adm_vwv.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * Security Configuration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  /**
   * Configures security settings for specific HTTP requests.
   *
   * @param http The {@link HttpSecurity} object to configure security settings.
   * @return A {@link SecurityFilterChain} configured with security settings.
   * @throws Exception If an exception occurs during security configuration.
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests(authorize ->
        authorize
          // --- PUBLIC ENDPOINTS ---
          .requestMatchers("/actuator/**", "/api/swagger-ui/**", "/environment")
          .permitAll()
          // --- SECURED ENDPOINTS ---
          .requestMatchers("/api/**")
          // TODO: Remove adm_vwv_user once role flow is implemented //NOSONAR
          .hasAnyRole("adm_user", "adm_vwv_user", "literature_user")
          // --- DENY ALL OTHERS ---
          .anyRequest()
          .denyAll()
      )
      .exceptionHandling(configurer ->
        configurer.defaultAuthenticationEntryPointFor(
          new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
          PathPatternRequestMatcher.withDefaults().matcher("/api/**")
        )
      )
      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(sessionManagement ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .headers(httpSecurityHeadersConfigurer ->
        httpSecurityHeadersConfigurer
          .contentSecurityPolicy(contentSecurityPolicyConfig ->
            contentSecurityPolicyConfig.policyDirectives(
              "default-src 'self'; " +
              "img-src 'self' data:; " +
              "style-src 'self' 'unsafe-inline'; " +
              "script-src 'self' 'unsafe-eval'; " +
              "connect-src 'self' https://neuris.login.bare.id *.sentry.io data:; " +
              "report-uri https://o1248831.ingest.us.sentry.io/api/4508482613084160/security/?sentry_key=7c56d29d5dd2c9bd48fc72a8edaffe57;"
            )
          )
          .contentTypeOptions(_ -> {})
          .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
          .referrerPolicy(referrerPolicyConfig ->
            referrerPolicyConfig.policy(
              ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
            )
          )
          .permissionsPolicyHeader(permissionsPolicyConfig ->
            permissionsPolicyConfig.policy(
              "accelerometer=(), ambient-light-sensor=(), autoplay=(), battery=(), camera=(), cross-origin-isolated=(), " +
              "display-capture=(), document-domain=(), encrypted-media=(), execution-while-not-rendered=(), " +
              "execution-while-out-of-viewport=(), fullscreen=(), geolocation=(), gyroscope=(), keyboard-map=(), " +
              "magnetometer=(), microphone=(), midi=(), navigation-override=(), payment=(), picture-in-picture=(), " +
              "publickey-credentials-get=(), screen-wake-lock=(), sync-xhr=(), usb=(), web-share=(), xr-spatial-tracking=(), " +
              "clipboard-read=(self), clipboard-write=(self), gamepad=(), speaker-selection=(), conversion-measurement=(), " +
              "focus-without-user-activation=(self), hid=(), idle-detection=(), interest-cohort=(), serial=(), sync-script=(), " +
              "trust-token-redemption=(), window-placement=(), vertical-scroll=(self)"
            )
          )
      );

    return http.build();
  }

  /**
   * A custom {@link JwtAuthenticationConverter} that extracts user roles from the Keycloak JWT.
   * <p>
   * By default, Keycloak includes user roles inside the "roles" array within the "realm_access" claim.
   * This converter retrieves those roles and transforms them into a list of {@link SimpleGrantedAuthority}
   * instances, prefixing each role with "ROLE_" to comply with Spring Security's expectations when using
   * the {@code .hasRole()} method.
   * </p>
   * <p>
   * The method suppresses unchecked warnings because Keycloak consistently provides the "realm_access" claim
   * as a {@code Map<String, Object>} containing a {@code List<String>} for roles. Since the format is well-defined
   * and predictable in Keycloak, the unchecked cast is safe in this specific context.
   * </p>
   *
   * @return a configured instance of {@link JwtAuthenticationConverter} that maps Keycloak roles to Spring Security authorities
   */
  @Bean
  @SuppressWarnings("unchecked")
  public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
    final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
      final Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
      if (realmAccess == null || realmAccess.isEmpty()) {
        return List.of();
      }
      final List<String> roles = (List<String>) realmAccess.get("roles");
      if (roles == null || roles.isEmpty()) {
        return List.of();
      }
      return roles
        .stream()
        .map(role -> "ROLE_" + role) // add prefix needed for .hasRole()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    };
    var jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }
}
