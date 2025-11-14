package de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

/**
 * Exception thrown when publishing a document to an external system fails.
 * <p>
 * Automatically results in an HTTP 503 response with a standardized
 * {@link ProblemDetail} body.
 */
public class PublishingFailedException extends ErrorResponseException {

  public PublishingFailedException(String message, Throwable cause) {
    super(
      HttpStatus.SERVICE_UNAVAILABLE,
      ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, message),
      cause
    );
  }
}
