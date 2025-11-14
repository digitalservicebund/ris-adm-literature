package de.bund.digitalservice.ris.adm_literature.application.converter.business;

import com.fasterxml.jackson.annotation.JsonAlias;
import de.bund.digitalservice.ris.adm_literature.application.DocumentType;
import de.bund.digitalservice.ris.adm_literature.application.ZitierArt;
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
 * @param zitierArt The citation type (Zitierart)
 */
public record ActiveCitation(
  UUID uuid,
  boolean newEntry,
  String documentNumber,
  Court court,
  String decisionDate,
  String fileNumber,
  DocumentType documentType,
  @JsonAlias("citationType") ZitierArt zitierArt
) {}
