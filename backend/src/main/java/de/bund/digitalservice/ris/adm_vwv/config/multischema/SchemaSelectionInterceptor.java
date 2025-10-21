package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts incoming HTTP requests to select the appropriate database schema for the current request.
 *
 */
@Component
public class SchemaSelectionInterceptor implements HandlerInterceptor {

  /**
   * Selects and sets the database schema for the current request based on the authenticated user's document type.
   *
   * <p>If the user is authenticated and the principal is an instance of {@link UserDocumentDetails}, the schema is
   * derived from the document type. Otherwise, it defaults to {@link SchemaType#ADM}.</p>
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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    SchemaType schemaToUse = SchemaType.ADM;

    if (
      authentication != null && authentication.getPrincipal() instanceof UserDocumentDetails details
    ) {
      schemaToUse = switch (details.type()) {
        case VERWALTUNGSVORSCHRIFTEN -> SchemaType.ADM;
        case LITERATUR_SELBSTSTAENDIG, LITERATUR_UNSELBSTSTAENDIG -> SchemaType.LIT;
      };
    }

    SchemaContextHolder.setSchema(schemaToUse);
    return true;
  }

  /**
   * Cleans up the schema context after the request has been processed.
   *
   * <p>Removes the schema from {@link SchemaContextHolder} to prevent leaking ThreadLocal state
   * across requests.</p>
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
    // Clean up the ThreadLocal after the request is complete
    SchemaContextHolder.clear();
  }
}
