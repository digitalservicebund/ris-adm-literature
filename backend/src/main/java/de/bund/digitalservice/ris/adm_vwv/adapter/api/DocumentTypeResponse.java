package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * DocumentTypes as returned by controller
 * 
 * @param documentTypes List of document types to be returned
 * @param page List of paginated document types to be returned
 */
public record DocumentTypeResponse(List<DocumentType> documentTypes, Page<DocumentType> page) {}
