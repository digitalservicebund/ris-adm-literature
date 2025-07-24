package de.bund.digitalservice.ris.adm_vwv.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the application.
 * This class uses @ControllerAdvice to provide centralized exception handling
 * across all controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles all uncaught exceptions.
   * Logs the full exception and returns a 500 Internal Server Error response to the client.
   *
   * @param ex The exception that was thrown.
   * @param request The current web request.
   * @return A ResponseEntity with a 500 status code and a generic error message.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
    String requestDescription = request.getDescription(true);
    LOG.error(
      "An unexpected error occurred for request {}: {}",
      requestDescription,
      ex.getMessage(),
      ex
    );
    return new ResponseEntity<>(
      "An internal server error occurred.",
      HttpStatus.INTERNAL_SERVER_ERROR
    );
  }
}
