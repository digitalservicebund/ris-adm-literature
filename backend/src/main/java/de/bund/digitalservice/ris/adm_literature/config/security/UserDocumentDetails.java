package de.bund.digitalservice.ris.adm_literature.config.security;

import de.bund.digitalservice.ris.adm_literature.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.application.DocumentationOffice;
import java.io.Serializable;

/**
 * Holds a user's assigned documentation office and document category.
 * <p>
 * This gets created from the user's JWT roles during login and is used as
 * the main user object.
 *
 * @param office The user's documentation office.
 * @param documentCategory   The document category of document the user works with.
 */
public record UserDocumentDetails(DocumentationOffice office, DocumentCategory documentCategory)
  implements Serializable {}
