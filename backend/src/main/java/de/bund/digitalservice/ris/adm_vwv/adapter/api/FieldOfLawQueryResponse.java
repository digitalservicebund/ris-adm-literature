package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import java.util.List;
import org.springframework.data.domain.Page;

public record FieldOfLawQueryResponse(List<FieldOfLaw> fieldsOfLaw, Page<FieldOfLaw> page) {}
