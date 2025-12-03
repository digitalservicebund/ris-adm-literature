package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business.DocumentationUnitContent;
import jakarta.annotation.Nonnull;

/**
 * A strategy for converting business models into LDML XML.
 */
public interface LdmlConverterStrategy {
  /**
   * Converts the given business model to its LDML XML representation.
   *
   * @param content            The documentation unit content to convert.
   * @param previousXmlVersion Previous XML version, or null if none exists.
   * @return LDML XML representation.
   */
  String convertToLdml(@Nonnull DocumentationUnitContent content, String previousXmlVersion);

  /**
   * Checks if this strategy can handle the given content type.
   *
   * @param content The content to check.
   * @return {@code true} if this strategy supports the content, {@code false} otherwise.
   */
  boolean supports(DocumentationUnitContent content);
}
