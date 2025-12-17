package de.bund.digitalservice.ris.adm_literature.documentation_unit;

/**
 * Represents an administrative record of an Aktivzitierung,
 * containing detailed metadata about the citation or document reference.
 *
 * @param documentNumber    The unique identifier of the document being activated.
 * @param citationType      The type of citation or referencing activity.
 * @param periodikum        The periodical or publication where the citation appears.
 * @param zitatstelle       The specific reference location (e.g., page, section) in the periodical.
 * @param inkrafttretedatum The effective date when the citation or document comes into effect.
 * @param aktenzeichen      The docket or case number associated with the document or process.
 * @param dokumenttyp       The document type categorizing the citation or document.
 * @param normgeber         The legislative or authoritative entity responsible for the cited content.
 */
public record AktivzitierungAdm(
  String documentNumber,
  String citationType,
  String periodikum,
  String zitatstelle,
  String inkrafttretedatum,
  String aktenzeichen,
  String dokumenttyp,
  String normgeber
) {}
