package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

/**
 * Represents an active reference relationship between two documentation units.
 * <p>
 *   An active reference defines a directed link where a source document explicitly
 *   points to a target document. It is the representation of a reversed document link:
 *   {@code source ---> target}.
 * </p>
 *
 * @param source The document that originates and holds the reference
 * @param target The document that is the destination of the reference
 * @see PassiveReference
 */
public record ActiveReference(DocumentReference source, DocumentReference target) {}
