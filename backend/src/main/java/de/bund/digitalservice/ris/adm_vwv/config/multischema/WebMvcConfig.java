package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Spring Web MVC components, primarily for registering custom interceptors.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final RequestCompletionInterceptor requestCompletionInterceptor;

  /**
   * Registers the {@link RequestCompletionInterceptor} to be applied to all incoming web requests.
   *
   * @param registry The interceptor registry to add the new interceptor to.
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestCompletionInterceptor);
  }
}
