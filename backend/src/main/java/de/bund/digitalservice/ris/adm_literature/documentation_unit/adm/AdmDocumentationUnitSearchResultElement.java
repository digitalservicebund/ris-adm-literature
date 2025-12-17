package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * ADM documentation unit search result element for the active citations
 *
 * @param id               The uuid of the documentation unit
 * @param documentNumber   The public id of the documentation unit
 * @param inkrafttretedatum The effective date when the citation or document comes into effect.
 * @param langueberschrift      The unabbreviated title of the documentation unit
 * @param dokumenttyp       The document type categorizing the citation or document.
 * @param normgeber         The legislative or authoritative entity responsible for the cited content.
 * @param periodikum        The periodical or publication where the citation appears.
 * @param zitatstelle       The specific reference location (e.g., page, section) in the periodical.
 * @param aktenzeichen      The docket or case number associated with the document or process.
 */
public record AdmDocumentationUnitSearchResultElement(
  @Nonnull UUID id,
  @Nonnull String documentNumber,
  String inkrafttretedatum,
  String langueberschrift,
  String dokumenttyp,
  String normgeber,
  String periodikum,
  String zitatstelle,
  String aktenzeichen
) {}
