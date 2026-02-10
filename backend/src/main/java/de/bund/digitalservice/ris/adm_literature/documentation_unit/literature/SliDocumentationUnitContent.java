package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungAdm;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungRechtsprechung;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungSli;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AktivzitierungUli;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import java.util.List;
import java.util.UUID;

/**
 * Business model for SLI documentation unit content.
 *
 * @param id                             The unique identifier.
 * @param documentNumber                 The document number.
 * @param veroeffentlichungsjahr         The publication year.
 * @param dokumenttypen                  The list of document types.
 * @param hauptsachtitel                 The main title.
 * @param hauptsachtitelZusatz           Additional information for the main title.
 * @param dokumentarischerTitel          The documentary title.
 * @param note                           An additional note.
 * @param aktivzitierungenSli            The list of active SLI references.
 * @param aktivzitierungenAdm            The list of active ADM references.
 * @param aktivzitierungenRechtsprechung The list of active Rechtsprechung references.
 * @param aktivzitierungenUli            The list of active ULI references.
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
  List<AktivzitierungSli> aktivzitierungenSli,
  List<AktivzitierungAdm> aktivzitierungenAdm,
  List<AktivzitierungRechtsprechung> aktivzitierungenRechtsprechung,
  List<AktivzitierungUli> aktivzitierungenUli
)
  implements LiteratureDocumentationUnitContent {
  public SliDocumentationUnitContent(
    SliDocumentationUnitContent sli,
    List<AktivzitierungAdm> aktivzitierungenAdm
  ) {
    this(
      sli.id,
      sli.documentNumber,
      sli.veroeffentlichungsjahr,
      sli.dokumenttypen,
      sli.hauptsachtitel,
      sli.hauptsachtitelZusatz,
      sli.dokumentarischerTitel,
      sli.note,
      sli.aktivzitierungenSli,
      aktivzitierungenAdm,
      sli.aktivzitierungenRechtsprechung,
      sli.aktivzitierungenUli
    );
  }

  @Override
  public DocumentCategory documentCategory() {
    return DocumentCategory.LITERATUR_SELBSTAENDIG;
  }
}
