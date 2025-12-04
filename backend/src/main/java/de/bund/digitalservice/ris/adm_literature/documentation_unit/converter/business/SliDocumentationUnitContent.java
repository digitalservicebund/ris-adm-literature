package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
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
 * @param aktivzitierungenSli The list of active SLI references.
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
  List<AktivzitierungSli> aktivzitierungenSli
)
  implements LiteratureDocumentationUnitContent {
  @Override
  public DocumentCategory documentCategory() {
    return DocumentCategory.LITERATUR_SELBSTAENDIG;
  }

  /**
   * A reference to an active SLI.
   * @param documentNumber The document number of the SLI.
   * @param veroeffentlichungsJahr The publication year of the SLI.
   * @param titel The main title of the SLI.
   * @param isbn The ISBN of the SLI.
   * @param verfasser The authors of the SLI.
   * @param dokumenttypen The type of the SLI.
   */
  // TODO: Optional and required fields need to be clarified NOSONAR
  public record AktivzitierungSli(
    String documentNumber,
    String veroeffentlichungsJahr,
    String titel,
    String isbn,
    List<String> verfasser, // urheber/ typ / verfasser need to be clarified
    List<DocumentType> dokumenttypen
  ) {}
}
