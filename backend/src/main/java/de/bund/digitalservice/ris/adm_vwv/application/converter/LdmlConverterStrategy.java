package de.bund.digitalservice.ris.adm_vwv.application.converter;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.IDocumentationContent;
import jakarta.annotation.Nonnull;

public interface LdmlConverterStrategy {
  /**
   * Converts the given business model to its LDML XML representation.
   *
   * @param content            The documentation unit content to convert.
   * @param previousXmlVersion Previous XML version, or null if none exists.
   * @return LDML XML representation.
   */
  String convertToLdml(@Nonnull IDocumentationContent content, String previousXmlVersion);

  /**
   * Checks if this strategy can handle the given content type.
   *
   * @param content The content to check.
   * @return {@code true} if this strategy supports the content, {@code false} otherwise.
   */
  boolean supports(IDocumentationContent content);
}
