package de.bund.digitalservice.ris.adm_vwv.application;

import java.util.List;

public record Fundstelle(String id, String zitatstelle, List<Periodikum> periodika) {}
