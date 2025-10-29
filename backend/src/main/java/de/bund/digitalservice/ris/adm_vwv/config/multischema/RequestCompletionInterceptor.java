package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationOffice;
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
 * Intercepts requests to log completion details and clean up the schema context.
 *
 * <p>This interceptor's primary logic is in {@link #afterCompletion},
 * where it logs the request outcome (status, user) and ensures {@link SchemaContextHolder} is cleared.
 * The actual schema selection and principal correction is handled earlier by
 * {@link TenantContextFilter}.</p>
 */
@Component
@Slf4j
public class RequestCompletionInterceptor implements HandlerInterceptor {

  /**
   * Passes the request through.
   *
   * <p>All pre-request logic, including schema selection and principal correction,
   * is now handled by {@link TenantContextFilter},
   * which runs earlier in the security filter chain.</p>
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
    // Logic was moved to TenantContextFilter
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
      // skip public endpoints
      if (
        !request.getRequestURI().startsWith("/environment") &&
        authentication != null &&
        authentication.getPrincipal() instanceof
        UserDocumentDetails(DocumentationOffice office, DocumentCategory documentCategory)
      ) {
        if (documentCategory != null) {
          logMessage.append(" documentationType=").append(documentCategory);
        }
        if (office != null) {
          logMessage.append(" documentationOffice=").append(office);
        }
      }

      String finalLogMessage = logMessage.toString();

      if (ex != null) {
        log.error("{} error='{}'", logMessage, ex.getMessage(), ex);
      } else {
        log.info(finalLogMessage);
      }
    }

    // Clean up the ThreadLocal after the request is complete
    SchemaContextHolder.clear();
  }
}
