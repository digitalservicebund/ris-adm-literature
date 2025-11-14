package de.bund.digitalservice.ris.adm_literature.lookup_tables.region;

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
class RegionServiceTest {

  @InjectMocks
  private RegionService regionService;

  @Mock
  private RegionRepository regionRepository;

  @Test
  void findRegions_all() {
    // given
    UUID uuid = UUID.randomUUID();
    RegionEntity regionEntity = new RegionEntity();
    regionEntity.setCode("AA");
    regionEntity.setId(uuid);
    given(regionRepository.findAll(any(Pageable.class))).willReturn(
      new PageImpl<>(List.of(regionEntity))
    );

    // when
    var regions = regionService.findRegions(
      new RegionQuery(null, new QueryOptions(0, 10, "code", Sort.Direction.ASC, true))
    );

    // then
    assertThat(regions.content()).contains(new Region(uuid, "AA", null));
  }

  @Test
  void findRegions_something() {
    // given
    UUID uuid = UUID.randomUUID();
    RegionEntity regionEntity = new RegionEntity();
    regionEntity.setCode("AA");
    regionEntity.setId(uuid);
    given(
      regionRepository.findByCodeContainingIgnoreCase(eq("something"), any(Pageable.class))
    ).willReturn(new PageImpl<>(List.of(regionEntity)));

    // when
    var regions = regionService.findRegions(
      new RegionQuery("something", new QueryOptions(0, 10, "name", Sort.Direction.ASC, true))
    );

    // then
    assertThat(regions.content()).contains(new Region(uuid, "AA", null));
  }
}
