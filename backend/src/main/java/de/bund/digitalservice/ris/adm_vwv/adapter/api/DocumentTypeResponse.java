package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import java.util.List;
import org.springframework.data.domain.Page;

public record DocumentTypeResponse(List<DocumentType> documentTypes, Page<DocumentType> page) {}
