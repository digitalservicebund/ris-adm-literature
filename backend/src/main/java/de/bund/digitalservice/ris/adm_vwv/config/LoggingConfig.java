package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Logs incoming requests and their responses.
 */
@Slf4j
@Configuration
public class LoggingConfig implements WebMvcConfigurer, HandlerInterceptor {

  /**
   * A nested filter class to prevent logging for actuator endpoints.
   */
  static class ConditionalRequestLoggingFilter extends CommonsRequestLoggingFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
      return request.getRequestURI().startsWith("/actuator");
    }
  }

  /**
   * Creates the @Bean for logging the request body, headers, etc.
   *
   * @return the log filter
   */
  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    ConditionalRequestLoggingFilter filter = new ConditionalRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    filter.setIncludeHeaders(true);
    filter.setIncludeClientInfo(true);
    filter.setAfterMessagePrefix("REQUEST DATA: ");
    return filter;
  }

  /**
   * Registers this class itself as the interceptor and excludes health checks.
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(this).excludePathPatterns("/actuator/**");
  }

  /**
   * Contains the logging logic that runs after the request is complete.
   */
  @Override
  public void afterCompletion(
    HttpServletRequest request,
    HttpServletResponse response,
    @Nonnull Object handler,
    Exception ex
  ) {
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
    if (
      authentication != null &&
      authentication.getPrincipal() instanceof UserDocumentDetails(var office, var type)
    ) {
      if (type != null) {
        logMessage.append(" documentationType=").append(type);
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
}
