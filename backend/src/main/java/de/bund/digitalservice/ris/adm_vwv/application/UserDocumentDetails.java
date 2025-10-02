package de.bund.digitalservice.ris.adm_vwv.application;

import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentTypeCode;
import de.bund.digitalservice.ris.adm_vwv.adapter.persistence.DocumentationOffice;
import java.io.Serializable;

/**
 * Holds a user's assigned documentation office and document type.
 * <p>
 * This gets created from the user's JWT roles during login and is used as
 * the main user object.
 *
 * @param office The user's documentation office.
 * @param type   The type of document the user works with.
 */
public record UserDocumentDetails(DocumentationOffice office, DocumentTypeCode type)
  implements Serializable {}
