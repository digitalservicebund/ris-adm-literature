package de.bund.digitalservice.ris.adm_vwv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Configuration for request logging.
 */
@Configuration
public class RequestLoggingFilterConfig {

  /**
   * Creates a CommonsRequestLoggingFilter bean to log incoming requests.
   *
   * @return The configured CommonsRequestLoggingFilter.
   */
  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    filter.setIncludeHeaders(false);
    filter.setAfterMessagePrefix("REQUEST DATA: ");
    return filter;
  }
}
