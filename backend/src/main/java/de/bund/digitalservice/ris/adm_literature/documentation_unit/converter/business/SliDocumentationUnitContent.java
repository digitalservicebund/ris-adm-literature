package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.List;
import java.util.UUID;

/**
 * Business model for SLI documentation unit content.
 *
 * @param id The unique identifier.
 * @param documentNumber The document number.
 * @param veroeffentlichungsjahr The publication year.
 * @param dokumenttypen The list of document types.
 * @param hauptsachtitel The main title.
 * @param hauptsachtitelZusatz Additional information for the main title.
 * @param dokumentarischerTitel The documentary title.
 * @param note An additional note.
 */
public record SliDocumentationUnitContent(
  UUID id,
  String documentNumber,
  String veroeffentlichungsjahr,
  List<DocumentType> dokumenttypen,
  String hauptsachtitel,
  String hauptsachtitelZusatz,
  String dokumentarischerTitel,
  String note,
  List<ActiveSliReference> activeSliReferences
)
  implements LiteratureDocumentationUnitContent {
  public record ActiveSliReference(
    String documentNumber,
    String veroeffentlichungsJahr,
    String hauptsachtitel,
    String isbn
  ) {}
}
