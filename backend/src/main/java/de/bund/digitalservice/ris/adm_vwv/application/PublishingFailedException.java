package de.bund.digitalservice.ris.adm_vwv.application;

public class PublishingFailedException extends RuntimeException {

  public PublishingFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
