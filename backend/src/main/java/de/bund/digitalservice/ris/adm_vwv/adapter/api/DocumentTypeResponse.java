package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import java.util.List;

/**
 * Response with DocumentTypes and pagination information
 *
 * @param documentTypes List of document types
 * @param page Pagination data
 */
public record DocumentTypeResponse(List<DocumentType> documentTypes, PageResponse page) {}
