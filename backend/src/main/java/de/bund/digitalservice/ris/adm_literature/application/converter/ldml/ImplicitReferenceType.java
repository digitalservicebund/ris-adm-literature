package de.bund.digitalservice.ris.adm_literature.application.converter.ldml;

/**
 * Type of implicit reference.
 */
public enum ImplicitReferenceType {
  /**
   * Reference type for 'Fundstellen', point to a legal periodical.
   */
  FUNDSTELLE,
  /**
   * Reference type for an active norm or ADM reference (in German 'Aktivverweisung').
   */
  ACTIVE_REFERENCE,
  /**
   * Reference type for active citation of caselaw documents (in German 'Aktivzitierung Rechtssprechung').
   */
  ACTIVE_CITATION,
}
