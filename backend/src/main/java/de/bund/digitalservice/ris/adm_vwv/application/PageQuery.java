package de.bund.digitalservice.ris.adm_vwv.application;

import javax.annotation.Nonnull;
import org.springframework.data.domain.Sort;

public record PageQuery(
  int page,
  int size,
  @Nonnull String sortBy,
  @Nonnull Sort.Direction sortDirection
) {}
