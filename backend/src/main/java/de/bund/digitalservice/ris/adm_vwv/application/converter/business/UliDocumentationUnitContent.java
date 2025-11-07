package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

/**
 * Business model for ULI documentation unit content.
 */
public record UliDocumentationUnitContent(
  UUID id,
  @NotBlank String documentNumber,
  @NotBlank String veroeffentlichungsjahr,
  List<DocumentType> dokumentTyp,
  String hauptsachtitel,
  String hauptsachtitelZusatz,
  String dokumentarischerTitel,
  String note
)
  implements IDocumentationContent {}
