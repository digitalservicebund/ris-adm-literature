package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static de.bund.digitalservice.ris.adm_vwv.util.DocumentTypeUtils.getDocumentTypeCode;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentTypeCode;
import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts incoming HTTP requests to select the appropriate database schema
 * and log request completion.
 */
@Component
@Slf4j
public class SchemaSelectionInterceptor implements HandlerInterceptor {

  /**
   * Selects and sets the database schema for the current request based on the authenticated user's document type.
   *
   * @param request the current HTTP request
   * @param response the current HTTP response
   * @param handler the chosen handler to execute
   * @return always {@code true} to continue the processing chain
   */
  @Override
  public boolean preHandle(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull Object handler
  ) {
    if ("/environment".equals(request.getRequestURI())) {
      log.debug("Skipping schema logic for /environment path");
      return true;
    }
    SchemaType schemaToUse;
    String headerDocumentType = request.getHeader("X-Document-Type");

    // TODO: Remove fallback adm logic once SchemaSelectionInterceptor is finalized ==> RISDEV-9947 // NOSONAR
    if (headerDocumentType != null) {
      try {
        DocumentTypeCode documentType = DocumentTypeCode.valueOf(headerDocumentType);
        schemaToUse = switch (documentType) {
          case VERWALTUNGSVORSCHRIFTEN -> SchemaType.ADM;
          case LITERATUR_SELBSTSTAENDIG, LITERATUR_UNSELBSTSTAENDIG -> SchemaType.LIT;
        };
      } catch (IllegalArgumentException _) {
        log.warn("Invalid X-Document-Type header value: {}", headerDocumentType);
        schemaToUse = SchemaType.ADM;
      }
    } else {
      log.warn("Missing X-Document-Type header, defaulting to ADM");
      schemaToUse = SchemaType.ADM;
    }

    log.info("Using schema {} for request", schemaToUse);
    SchemaContextHolder.setSchema(schemaToUse);
    return true;
  }

  /**
   * Logs request completion details and cleans up the schema context.
   *
   * <p>Logs the request method, URI, response status, and context-specific details.
   * Also removes the schema from {@link SchemaContextHolder} to prevent leaking
   * ThreadLocal state across requests.</p>
   *
   * @param request the current HTTP request
   * @param response the current HTTP response
   * @param handler the executed handler
   * @param ex any exception thrown on handler execution, if any
   */
  @Override
  public void afterCompletion(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull Object handler,
    Exception ex
  ) {
    if (request.getRequestURI() != null && !request.getRequestURI().startsWith("/actuator")) {
      StringBuilder logMessage = new StringBuilder();
      logMessage.append(
        String.format(
          "method=%s uri=%s status=%d",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus()
        )
      );

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (!request.getRequestURI().startsWith("/environment")) {
        DocumentTypeCode documentType = getDocumentTypeCode();
        if (documentType != null) {
          logMessage.append(" documentationType=").append(documentType);
        }
        if (
          authentication != null &&
          authentication.getPrincipal() instanceof UserDocumentDetails details &&
          details.office() != null
        ) {
          logMessage.append(" documentationOffice=").append(details.office());
        }
      }

      String finalLogMessage = logMessage.toString();

      if (ex != null) {
        // Log with the full context, including the exception
        log.error("{} error='{}'", logMessage, ex.getMessage(), ex);
      } else {
        // Log summary on success
        log.info(finalLogMessage);
      }
    }

    // Clean up the ThreadLocal after the request is complete
    SchemaContextHolder.clear();
  }
}
