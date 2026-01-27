package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * Represents an administrative record of an Aktivzitierung Rechtsprechung,
 * containing detailed metadata about the citation or document reference.
 *
 * @param id Id of the reference
 * @param documentNumber    The unique identifier of the referenced document.
 * @param citationType      The type of citation or referencing activity.
 * @param entscheidungsdatum The decision date of the referenced document.
 * @param aktenzeichen      The docket or case number associated with the document or process.
 * @param dokumenttyp       The document type categorizing the citation or document.
 * @param gericht         The court responsible for the cited content.
 */
public record AktivzitierungRechtsprechung(
  @Nonnull UUID id,
  String documentNumber,
  String citationType,
  String entscheidungsdatum,
  String aktenzeichen,
  String dokumenttyp,
  String gericht
) {}
