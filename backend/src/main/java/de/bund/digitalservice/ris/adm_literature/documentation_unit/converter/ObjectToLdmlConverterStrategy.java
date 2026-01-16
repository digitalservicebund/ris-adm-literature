package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitContent;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.DocumentReference;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * A strategy for converting business models into LDML XML.
 */
public interface ObjectToLdmlConverterStrategy {
  /**
   * Converts the given business model to its LDML XML representation.
   *
   * @param content            The documentation unit content to convert.
   * @param previousXmlVersion Previous XML version, or null if none exists.
   * @param referencedByList List of documents which are referencing the given document (passive references)
   * @return LDML XML representation.
   */
  String convertToLdml(
    @Nonnull DocumentationUnitContent content,
    String previousXmlVersion,
    List<DocumentReference> referencedByList
  );

  /**
   * Checks if this strategy can handle the given content type.
   *
   * @param content The content to check.
   * @return {@code true} if this strategy supports the content, {@code false} otherwise.
   */
  boolean supports(DocumentationUnitContent content);
}
