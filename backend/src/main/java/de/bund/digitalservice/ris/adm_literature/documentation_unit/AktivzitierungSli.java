package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * A reference to an active SLI.
 *
 * @param id Id of the reference
 * @param documentNumber         The document number of the SLI.
 * @param veroeffentlichungsJahr The publication year of the SLI.
 * @param titel                  The main title of the SLI.
 * @param isbn                   The ISBN of the SLI.
 * @param verfasser              The authors of the SLI.
 * @param dokumenttypen          The type of the SLI.
 */
// TODO: Optional and required fields need to be clarified NOSONAR
public record AktivzitierungSli(
  @Nonnull UUID id,
  String documentNumber,
  String veroeffentlichungsJahr,
  String titel,
  String isbn,
  List<String> verfasser, // urheber/ typ / verfasser need to be clarified
  List<DocumentType> dokumenttypen
) {}
