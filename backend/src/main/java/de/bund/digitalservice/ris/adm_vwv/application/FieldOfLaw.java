package de.bund.digitalservice.ris.adm_vwv.application;

import lombok.Builder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record FieldOfLaw(
    UUID id,
    boolean hasChildren,
    String identifier,
    String text,
    List<String> linkedFields,
    List<Norm> norms,
    List<FieldOfLaw> children,
    @Nullable FieldOfLaw parent) {}
