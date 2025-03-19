package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

public record DocumentTypeResponse(List<DocumentType> documentTypes, Page<DocumentType> page) {}
