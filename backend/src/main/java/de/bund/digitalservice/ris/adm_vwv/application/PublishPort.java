package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;

/**
 * A port for publishing documentation unit content to an external storage system.
 */
public interface PublishPort {
  /**
   * Publishes the provided content to the configured storage.
   *
   * @param options The options containing the content and identifier.
   */
  void publish(@Nonnull Options options);

  record Options(String documentNumber, String xmlContent) {}
}
