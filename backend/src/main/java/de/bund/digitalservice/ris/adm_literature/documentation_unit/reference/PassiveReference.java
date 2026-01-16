package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

/**
 * Represents a passive reference relationship between two documentation units.
 * <p>
 *   A passive reference is the counterpart to an active reference. While the active reference defines which source
 *   document is referencing which target, the passive reference defines which target document is being referenced by
 *   which source: {@code target <--- referencedBy}.
 * </p>
 * <h4>Reference Perspective Overview</h4>
 * <table border="1" style="width:100%; border-collapse:collapse; border:1px solid gray;">
 * <tr style="background-color: rgba(128, 128, 128, 0.15);">
 * <th style="padding:8px; text-align:left;">Type</th>
 * <th style="padding:8px; text-align:left;">First Field</th>
 * <th style="padding:8px; text-align:left;">Second Field</th>
 * <th style="padding:8px; text-align:left;">Logical Meaning</th>
 * </tr>
 * <tr>
 * <td style="padding:8px;"><b>Active</b></td>
 * <td style="padding:8px;">{@code source}</td>
 * <td style="padding:8px;">{@code target}</td>
 * <td style="padding:8px;">I am pointing <b>to</b>...</td>
 * </tr>
 * <tr>
 * <td style="padding:8px;"><b>Passive</b></td>
 * <td style="padding:8px;">{@code target}</td>
 * <td style="padding:8px;">{@code referencedBy}</td>
 * <td style="padding:8px;">I am referenced <b>by</b>...</td>
 * </tr>
 * </table>
 *
 * @param target The document that is the destination of the reference
 * @param referencedBy The source document that holds or originates the reference
 * @see ActiveReference
 */
public record PassiveReference(DocumentReference target, DocumentReference referencedBy) {}
