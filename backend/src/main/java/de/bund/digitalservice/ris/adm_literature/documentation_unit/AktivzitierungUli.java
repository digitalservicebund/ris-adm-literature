package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentType;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * A reference to an active ULI.
 *
 * @param id Id of the reference
 * @param documentNumber         The document number of the ULI.
 * @param periodikum        The periodical or publication where the citation appears.
 * @param zitatstelle       The specific reference location (e.g., page, section) in the periodical.
 * @param verfasser              The authors of the ULI.
 * @param dokumenttypen          The document type of the ULI.
 */
public record AktivzitierungUli(
  @Nonnull UUID id,
  String documentNumber,
  String periodikum,
  String zitatstelle,
  List<String> verfasser,
  List<DocumentType> dokumenttypen
) {}
