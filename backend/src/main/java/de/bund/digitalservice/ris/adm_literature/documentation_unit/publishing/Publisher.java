package de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import javax.annotation.Nonnull;

/**
 * An interface for publishing documentation unit content to an external system.
 */
public interface Publisher {
  /**
   * Identifies the correct publisher
   * @return the name of the publisher
   */
  String getName();

  /**
   * Publishes the provided content to the configured storage.
   *
   * @param publicationDetails The options containing the content and identifier.
   */
  void publish(@Nonnull PublicationDetails publicationDetails);

  /**
   * A data record holding the necessary information for publishing a document.
   *
   * @param documentNumber The unique identifier for the document, used as the storage key.
   * @param xmlContent The LDML XML content of the document to be stored.
   * @param targetPublisher The target publisher
   */
  record PublicationDetails(
    String documentNumber,
    String xmlContent,
    String targetPublisher,
    DocumentCategory category
  ) {}
}
