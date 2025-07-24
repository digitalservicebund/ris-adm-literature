package de.bund.digitalservice.ris.adm_vwv.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
    LOG.error(
      "An unexpected error occurred for request {}: {}",
      request.getDescription(true),
      ex.getMessage(),
      ex
    );
    return new ResponseEntity<>(
      "An internal server error occurred.",
      HttpStatus.INTERNAL_SERVER_ERROR
    );
  }
}
