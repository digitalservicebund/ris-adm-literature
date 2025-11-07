package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

/**
 * Business model for ULI documentation unit content.
 *
 * @param id The unique identifier.
 * @param documentNumber The document number.
 * @param veroeffentlichungsjahr The publication year.
 * @param dokumentTyp The list of document types.
 * @param hauptsachtitel The main title.
 * @param hauptsachtitelZusatz Additional information for the main title.
 * @param dokumentarischerTitel The documentary title.
 * @param note An additional note.
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
