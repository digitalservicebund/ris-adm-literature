package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import org.springframework.data.domain.Page;

import java.util.List;

public record FieldOfLawQueryResponse(List<FieldOfLaw> fieldsOfLaw, Page<FieldOfLaw> page) {
}
