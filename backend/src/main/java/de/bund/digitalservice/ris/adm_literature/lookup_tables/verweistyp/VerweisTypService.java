package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import de.bund.digitalservice.ris.adm_literature.page.Page;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for lookup table 'Verweistyp'.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VerweisTypService {

  /**
   * Finds a paginated list of reference types (VerweisTypen) (currently mocked).
   *
   * @param query The query, which is currently ignored.
   * @return A page of mocked {@link VerweisTyp}.
   */
  @Transactional(readOnly = true)
  public Page<VerweisTyp> findVerweisTypen(@Nonnull VerweisTypQuery query) {
    log.info(
      "Ignoring given query as mocked reference types result is always returned: {}.",
      query
    );
    return new Page<>(
      List.of(
        new VerweisTyp(UUID.fromString("3b0c6c8c-bb5d-4c18-9d1d-6d3c93e88f45"), "anwendung"),
        new VerweisTyp(UUID.fromString("c8a27a4a-79d9-4f28-b462-47eeb03b6b6f"), "neuregelung"),
        new VerweisTyp(UUID.fromString("5e7c24f7-85f4-4e89-9113-4d5eae1b29d3"), "rechtsgrundlage")
      ),
      3,
      0,
      3,
      3,
      true,
      true,
      false
    );
  }
}
