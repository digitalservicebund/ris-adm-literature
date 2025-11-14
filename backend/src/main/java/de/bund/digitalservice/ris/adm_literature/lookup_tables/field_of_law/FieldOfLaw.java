package de.bund.digitalservice.ris.adm_literature.lookup_tables.field_of_law;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

/**
 * Field of law business record.
 *
 * @param id           UUID of field of law
 * @param hasChildren  {@code true} if this field of law has children, otherwise
 *                     {@code false}
 * @param identifier   Identifier of field of law
 * @param text         Text
 * @param notation     Either NEW or OLD depending on sachgdata
 * @param linkedFields List of field of law identifiers which are linked inside
 *                     {@code text}
 * @param norms        List of norms (can be empty)
 * @param children     List of children (can be empty)
 * @param parent       The parent field of law (can be null)
 */
@Builder(toBuilder = true)
public record FieldOfLaw(
  @Nonnull UUID id,
  boolean hasChildren,
  @Nonnull String identifier,
  @Nonnull String text,
  @Nonnull String notation,
  @Nonnull List<String> linkedFields,
  @Nonnull List<FieldOfLawNorm> norms,
  @Nonnull List<FieldOfLaw> children,
  FieldOfLaw parent
) {}
