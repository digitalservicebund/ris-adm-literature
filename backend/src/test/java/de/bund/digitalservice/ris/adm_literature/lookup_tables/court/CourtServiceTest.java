package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class CourtServiceTest {

  @InjectMocks
  private CourtService courtService;

  @Test
  void findCourts_all() {
    // given

    // when
    var courts = courtService.findCourts(
      new CourtQuery(null, new QueryOptions(0, 10, "type", Sort.Direction.ASC, true))
    );

    // then
    assertThat(courts.content())
      .extracting(Court::type, Court::location)
      .containsExactly(
        Tuple.tuple("AG", "Aachen"),
        Tuple.tuple("Berufsgericht für Architekten", "Bremen")
      );
  }
}
