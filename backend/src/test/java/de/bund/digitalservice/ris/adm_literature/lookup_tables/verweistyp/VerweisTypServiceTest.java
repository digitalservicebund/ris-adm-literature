package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class VerweisTypServiceTest {

  @InjectMocks
  private VerweisTypService verweisTypService;

  @Mock
  private VerweisTypRepository verweisTypRepository;

  @Test
  void findVerweisTypen_all() {
    // given
    UUID uuid = UUID.randomUUID();
    VerweisTypEntity verweisTypEntity = new VerweisTypEntity();
    verweisTypEntity.setId(uuid);
    verweisTypEntity.setName("Anwendung");
    verweisTypEntity.setTypNummer("01");
    given(verweisTypRepository.findAll(any(Pageable.class))).willReturn(
      new PageImpl<>(List.of(verweisTypEntity))
    );

    // when
    var verweisTypen = verweisTypService.findVerweisTypen(
      new VerweisTypQuery(null, new QueryOptions(0, 10, "name", Sort.Direction.ASC, true))
    );

    // then
    assertThat(verweisTypen.content()).contains(new VerweisTyp(uuid, "Anwendung", "01"));
  }

  @Test
  void findVerweisTypen_something() {
    // given
    UUID uuid = UUID.randomUUID();
    VerweisTypEntity verweisTypEntity = new VerweisTypEntity();
    verweisTypEntity.setId(uuid);
    verweisTypEntity.setName("Neuregelung");
    verweisTypEntity.setTypNummer("31");
    given(
      verweisTypRepository.findByNameContainingIgnoreCase(eq("neureg"), any(Pageable.class))
    ).willReturn(new PageImpl<>(List.of(verweisTypEntity)));

    // when
    var verweisTypen = verweisTypService.findVerweisTypen(
      new VerweisTypQuery("neureg", new QueryOptions(0, 10, "name", Sort.Direction.ASC, true))
    );

    // then
    assertThat(verweisTypen.content()).contains(new VerweisTyp(uuid, "Neuregelung", "31"));
  }
}
