package de.bund.digitalservice.ris.adm_vwv.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class PublishingFailedException extends ErrorResponseException {

  public PublishingFailedException(String message, Throwable cause) {
    super(
      HttpStatus.SERVICE_UNAVAILABLE,
      ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, message),
      cause
    );
  }
}
