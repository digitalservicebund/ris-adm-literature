package de.bund.digitalservice.ris.adm_vwv.application.converter.ldml;

/**
 * Type of implicit reference.
 */
public enum ImplicitReferenceType {
  /**
   * Reference type for 'Fundstellen', point to a legal periodical.
   */
  FUNDSTELLE,
  /**
   * Reference type for an active norm ord adm reference (in German 'Aktivverweisung').
   */
  NORM,
  /**
   * Reference type for active citation of caselaw documents (in German 'Aktivzitierung Rechtssprechung').
   */
  CASELAW,
}
