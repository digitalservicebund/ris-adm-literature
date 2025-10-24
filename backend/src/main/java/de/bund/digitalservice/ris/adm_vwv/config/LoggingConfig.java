package de.bund.digitalservice.ris.adm_vwv.config;

import de.bund.digitalservice.ris.adm_vwv.config.multischema.RequestCompletionInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Logs incoming request payloads.
 * Response logging is handled by {@link RequestCompletionInterceptor}.
 */
@Slf4j
@Configuration
public class LoggingConfig implements WebMvcConfigurer {

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
}
