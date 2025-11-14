package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import de.bund.digitalservice.ris.adm_literature.page.Page;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for court lookup tables.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CourtService {

  /**
   * Finds a paginated list of courts (currently mocked).
   *
   * @param query The query, which is currently ignored.
   * @return A page of mocked {@link Court}.
   */
  @Transactional(readOnly = true)
  public Page<Court> findCourts(@Nonnull CourtQuery query) {
    log.info("Ignoring given query as mocked courts result is always returned: {}.", query);
    return new Page<>(
      List.of(
        new Court(UUID.fromString("0e1b035-a7f4-4d88-b5c0-a7d0466b8752"), "AG", "Aachen"),
        new Court(
          UUID.fromString("8163531c-2c51-410a-9591-b45b004771da"),
          "Berufsgericht für Architekten",
          "Bremen"
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
