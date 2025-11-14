package de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type;

import de.bund.digitalservice.ris.adm_literature.page.PageResponse;
import java.util.List;

/**
 * Response with DocumentTypes and pagination information
 *
 * @param documentTypes List of document types
 * @param page Pagination data
 */
public record DocumentTypeResponse(List<DocumentType> documentTypes, PageResponse page) {}
