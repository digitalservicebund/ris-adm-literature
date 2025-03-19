package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;

public record DocumentTypeQuery(@Nonnull String searchQuery, @Nonnull PageQuery pageQuery) {}
