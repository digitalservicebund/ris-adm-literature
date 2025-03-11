package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import java.util.List;

public record DocumentTypeResponse(List<DocumentType> documentTypes) {}
