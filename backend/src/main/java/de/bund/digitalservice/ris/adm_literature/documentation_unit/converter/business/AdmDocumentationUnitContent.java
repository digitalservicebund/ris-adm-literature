package de.bund.digitalservice.ris.adm_literature.documentation_unit.converter.business;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.Fundstelle;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law.FieldOfLaw;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * Documentation unit content record.
 *
 * @param id                    The uuid
 * @param documentNumber        The document number
 * @param fundstellen           List of fundstellen
 * @param fieldsOfLaw           List of fields of law
 * @param langueberschrift      Long title
 * @param keywords              List of keywords
 * @param zitierdaten           List of Date of quotes
 * @param inkrafttretedatum     Date of entry into effect
 * @param ausserkrafttretedatum Date of expiration
 * @param gliederung            Table of contents
 * @param kurzreferat           Preface
 * @param aktenzeichen          List of reference numbers
 * @param dokumenttyp           The document type
 * @param dokumenttypZusatz     Document type description
 * @param activeCitations       List of active citations
 * @param activeReferences      List of active references
 * @param normReferences        List of norm references
 * @param note                  The note
 * @param normgeberList         List of normgeber
 * @param berufsbilder          List of berufsbild
 * @param titelAspekte          List of titelAspekt
 * @param definitionen          List of definition
 */
public record AdmDocumentationUnitContent(
  UUID id,
  String documentNumber,
  List<Fundstelle> fundstellen,
  List<FieldOfLaw> fieldsOfLaw,
  @NotBlank String langueberschrift,
  List<String> keywords,
  @NotEmpty List<String> zitierdaten,
  @NotBlank String inkrafttretedatum,
  String ausserkrafttretedatum,
  String gliederung,
  String kurzreferat,
  List<String> aktenzeichen,
  @NotNull DocumentType dokumenttyp,
  String dokumenttypZusatz,
  @Valid List<ActiveCitation> activeCitations,
  List<ActiveReference> activeReferences,
  List<NormReference> normReferences,
  String note,
  @NotEmpty List<Normgeber> normgeberList,
  List<String> berufsbilder,
  List<String> titelAspekte,
  List<Definition> definitionen
)
  implements DocumentationUnitContent {
  @Override
  public DocumentCategory documentCategory() {
    return DocumentCategory.VERWALTUNGSVORSCHRIFTEN;
  }
}
