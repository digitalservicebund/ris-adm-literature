package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.List;
import java.util.UUID;

/**
 * Business model for ULI documentation unit content.
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
public record UliDocumentationUnitContent(
  UUID id,
  String documentNumber,
  String veroeffentlichungsjahr,
  List<DocumentType> dokumenttypen,
  String hauptsachtitel,
  String hauptsachtitelZusatz,
  String dokumentarischerTitel,
  String note
)
  implements LiteratureDocumentationUnitContent {
  @Override
  public DocumentCategory documentCategory() {
    return DocumentCategory.LITERATUR_UNSELBSTAENDIG;
  }
}
