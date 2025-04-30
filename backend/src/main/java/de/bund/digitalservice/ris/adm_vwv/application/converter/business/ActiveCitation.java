package de.bund.digitalservice.ris.adm_vwv.application.converter.business;

import java.util.UUID;

/**
 * Active citation record.
 *
 * @param uuid The uuid
 * @param newEntry {@code true} if this is a new entry, {@code false} otherwise
 * @param documentNumber The document number of the cited document
 * @param court The court
 * @param decisionDate The decision date
 * @param fileNumber The file number
 * @param documentType The document type
 * @param citationType The citation type
 */
public record ActiveCitation(
  UUID uuid,
  boolean newEntry,
  String documentNumber,
  Court court,
  String decisionDate,
  String fileNumber,
  DocumentTypeContent documentType,
  CitationType citationType
) {}
