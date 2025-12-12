package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import jakarta.annotation.Nonnull;

/**
 * A strategy for converting LDML XML into business models.
 */
public interface LdmlToObjectConverterStrategy {
  /**
   * Converts the xml of the given documentation unit to a business model.
   *
   * @param documentationUnit The documentation unit to convert
   * @return Business model representation of given documentation unit's xml
   */
  DocumentationUnitContent convertToBusinessModel(@Nonnull DocumentationUnit documentationUnit);

  /**
   * Checks if this strategy can convert xml to the given class type.
   *
   * @param clazz The business model class to support
   * @return {@code true} if this strategy supports the content, {@code false} otherwise.
   */
  boolean supports(Class<? extends DocumentationUnitContent> clazz);
}
