package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SchemaSelectionInterceptor implements HandlerInterceptor {

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
        case ADMINISTRATIVE -> SchemaType.ADM;
        case LITERATURE_DEPENDENT, LITERATURE_INDEPENDENT -> SchemaType.LIT;
      };
    }

    SchemaContextHolder.setSchema(schemaToUse);
    return true;
  }

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
