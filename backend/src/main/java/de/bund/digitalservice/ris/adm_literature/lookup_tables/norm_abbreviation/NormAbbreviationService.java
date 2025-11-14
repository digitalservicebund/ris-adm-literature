package de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation;

import de.bund.digitalservice.ris.adm_literature.page.Page;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for lookup table norm abbreviation (in German 'Normabkürzung').
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NormAbbreviationService {

  /**
   * Finds a paginated list of norm abbreviations (currently mocked).
   *
   * @param query The query, which is currently ignored.
   * @return A page of mocked {@link NormAbbreviation}.
   */
  @Transactional(readOnly = true)
  public Page<NormAbbreviation> findNormAbbreviations(@Nonnull NormAbbreviationQuery query) {
    log.info(
      "Ignoring given query as mocked norm abbreviations result is always returned: {}.",
      query
    );
    return new Page<>(
      List.of(
        new NormAbbreviation(
          UUID.fromString("3f7c912-a2d1-4b3e-9d2a-41c2a8e5c1f7"),
          "SGB 5",
          "Sozialgesetzbuch (SGB) Fünftes Buch (V)"
        ),
        new NormAbbreviation(
          UUID.fromString("d9a04e5-b7c8-49f2-8a31-7fb024b39ce8"),
          "KVLG",
          "Gesetz zur Weiterentwicklung des Rechts der gesetzlichen Krankenversicherung"
        )
      ),
      2,
      0,
      2,
      2,
      true,
      true,
      false
    );
  }
}
