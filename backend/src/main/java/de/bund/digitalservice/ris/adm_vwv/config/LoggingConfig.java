package de.bund.digitalservice.ris.adm_vwv.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Logs incoming requests and their responses.
 */
@Configuration
public class LoggingConfig implements WebMvcConfigurer, HandlerInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(LoggingConfig.class);

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
    @NotNull Object handler,
    Exception ex
  ) {
    String logMessage = String.format(
      "method=%s uri=%s status=%d",
      request.getMethod(),
      request.getRequestURI(),
      response.getStatus()
    );

    if (ex != null) {
      LOG.error("{} error='{}'", logMessage, ex.getMessage(), ex);
    } else {
      LOG.info(logMessage);
    }
  }
}
