package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.converter.jaxb;

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
  /**
   * Reference type for passive references from SLI.
   */
  SLI_PASSIVE_REFERENCE,
}
