package de.bund.digitalservice.ris.adm_vwv.config;

import io.sentry.Hint;
import io.sentry.SamplingContext;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * A central component for Sentry callbacks to control performance sampling and error filtering.
 * <p>
 * This class implements both {@link SentryOptions.TracesSamplerCallback} to decide which transactions
 * to sample and {@link SentryOptions.BeforeSendCallback} to filter error events before they are sent.
 */
@Component
public class SentryCallbackFilter
  implements SentryOptions.TracesSamplerCallback, SentryOptions.BeforeSendCallback {

  /**
   * Decides the sampling rate for performance transactions.
   * <p>
   * This implementation filters out transactions for Actuator endpoints.
   *
   * @param context The sample
   * @return A sample rate of 0.0 to discard the transaction, or {@code null} to use the default rate.
   */
  @Override
  public Double sample(SamplingContext context) {
    if (context.getCustomSamplingContext() != null) {
      HttpServletRequest request = (HttpServletRequest) context
        .getCustomSamplingContext()
        .get("request");

      if (request != null) {
        String url = request.getRequestURI();
        if (url.startsWith("/actuator/")) {
          return 0.0; // Discard actuator pings
        }
      }
    }
    return null;
  }

  /**
   * Filters Sentry error events before they are sent to the server.
   * <p>
   * This implementation inspects the exception that caused the event. If the exception
   * corresponds to a 4xx client error status code, the event is discarded.
   *
   * @param event The Sentry event being processed.
   * @param hint  A hint object that contains the original exception.
   * @return The original event to send it, or {@code null} to discard it.
   */
  @Override
  public SentryEvent execute(@NotNull SentryEvent event, Hint hint) {
    Throwable throwable = null;
    Object throwableHint = hint.get("throwable");
    if (throwableHint instanceof Throwable) {
      throwable = (Throwable) throwableHint;
    }

    if (throwable instanceof ResponseStatusException) {
      HttpStatusCode status = ((ResponseStatusException) throwable).getStatusCode();

      if (status.is4xxClientError()) {
        return null; // Discard 4xx error events
      }
    }

    return event;
  }
}
