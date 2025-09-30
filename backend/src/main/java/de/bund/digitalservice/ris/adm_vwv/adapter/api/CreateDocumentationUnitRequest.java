package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDocumentationUnitRequest {

  @NotBlank
  private String documentationOffice;
}
