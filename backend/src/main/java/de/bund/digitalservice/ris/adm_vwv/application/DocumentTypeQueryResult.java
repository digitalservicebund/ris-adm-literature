package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import java.util.List;

public record DocumentTypeQueryResult(@Nonnull List<DocumentType> documentTypes, @Nonnull Pagination pagination) {}
