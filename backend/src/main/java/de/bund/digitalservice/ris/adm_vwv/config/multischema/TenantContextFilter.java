package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A filter that runs after authentication to:
 * 1. Read the 'X-Document-Type' header.
 * 2. Update the SecurityContext principal with the *active* DocumentTypeCode.
 * 3. Set the database schema in SchemaContextHolder for the current request.
 * 4. Clean up the SchemaContextHolder after the request.
 */
@Component
@Slf4j
public class TenantContextFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String requestURI = request.getRequestURI();
    if (
      "/environment".equals(requestURI) ||
      (requestURI != null && requestURI.startsWith("/actuator"))
    ) {
      log.debug("Skipping tenant/schema logic for public path: {}", requestURI);
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String headerDocumentType = request.getHeader("X-Document-Type");
      SchemaType schemaToUse;
      DocumentCategory activeDocumentCategory = null;

      if (headerDocumentType != null) {
        try {
          activeDocumentCategory = DocumentCategory.valueOf(headerDocumentType);
          schemaToUse = switch (activeDocumentCategory) {
            case VERWALTUNGSVORSCHRIFTEN -> SchemaType.ADM;
            case LITERATUR_SELBSTSTAENDIG, LITERATUR_UNSELBSTSTAENDIG -> SchemaType.LIT;
          };
        } catch (IllegalArgumentException _) {
          log.warn("Invalid X-Document-Type header value: {}", headerDocumentType);
          schemaToUse = SchemaType.ADM; // Default to ADM on invalid header
        }
      } else {
        log.warn("Missing X-Document-Type header for URI: {}. Defaulting to ADM.", requestURI);
        schemaToUse = SchemaType.ADM;
      }

      // Update the Security Principal
      if (activeDocumentCategory != null) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication oldAuth = context.getAuthentication();

        if (
          oldAuth != null &&
          oldAuth.isAuthenticated() &&
          oldAuth.getPrincipal() instanceof UserDocumentDetails oldDetails
        ) {
          UserDocumentDetails newDetails = new UserDocumentDetails(
            oldDetails.office(),
            activeDocumentCategory
          );

          Authentication newAuth = new UsernamePasswordAuthenticationToken(
            newDetails,
            oldAuth.getCredentials(),
            oldAuth.getAuthorities()
          );

          context.setAuthentication(newAuth);
          log.debug(
            "Updated SecurityContext principal with active DocumentType: {}",
            activeDocumentCategory
          );
        }
      }

      log.info("Using schema {} for request: {}", schemaToUse, requestURI);
      SchemaContextHolder.setSchema(schemaToUse);

      filterChain.doFilter(request, response);
    } finally {
      SchemaContextHolder.clear();
      log.debug("Cleared SchemaContextHolder");
    }
  }
}
