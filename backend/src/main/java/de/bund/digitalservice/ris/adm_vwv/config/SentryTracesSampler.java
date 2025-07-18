package de.bund.digitalservice.ris.adm_vwv.config;

import io.sentry.SamplingContext;
import io.sentry.SentryOptions.TracesSamplerCallback;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
class SentryTracesSampler implements TracesSamplerCallback {

  @Override
  public Double sample(SamplingContext context) {
    if (context.getCustomSamplingContext() != null) {
      HttpServletRequest request = (HttpServletRequest) context
        .getCustomSamplingContext()
        .get("request");

      if (request != null) {
        String url = request.getRequestURI();
        if (url.startsWith("/actuator/")) {
          return 0.0; // Discard actuator transaction
        }
      }
    }

    return null;
  }
}
