package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

  @InjectMocks
  private CourtService courtService;

  @Mock
  private CourtRepository courtRepository;

  @Test
  void findCourts_all_whenSearchTermIsBlank() {
    // given
    var court1 = new CourtEntity();
    court1.setId(UUID.randomUUID());
    court1.setType("AG");
    court1.setLocation("Aachen");

    var court2 = new CourtEntity();
    court2.setId(UUID.randomUUID());
    court2.setType("Berufsgericht für Architekten");
    court2.setLocation("Bremen");

    when(courtRepository.findAll(any(Pageable.class))).thenReturn(
      new PageImpl<>(List.of(court1, court2))
    );

    var query = new CourtQuery(null, new QueryOptions(0, 10, "type", Sort.Direction.ASC, true));

    // when
    var courts = courtService.findCourts(query);

    // then
    assertThat(courts.content())
      .extracting(Court::type, Court::location)
      .containsExactly(
        Tuple.tuple("AG", "Aachen"),
        Tuple.tuple("Berufsgericht für Architekten", "Bremen")
      );
  }

  @Test
  void findCourts_withSearchTerm() {
    // given
    String search = "München";
    var court = new CourtEntity();
    court.setType("LG");
    court.setLocation("München");

    when(
      courtRepository.findByTypeContainingIgnoreCaseOrLocationContainingIgnoreCase(
        eq(search),
        eq(search),
        any(Pageable.class)
      )
    ).thenReturn(new PageImpl<>(List.of(court)));

    var query = new CourtQuery(search, new QueryOptions(0, 10, "type", Sort.Direction.ASC, true));

    // when
    var courts = courtService.findCourts(query);

    // then
    assertThat(courts.content()).extracting(Court::location).containsExactly("München");
  }
}
