package de.bund.digitalservice.ris.adm_literature.lookup_tables.institution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.Region;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.region.RegionEntity;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

  @InjectMocks
  private InstitutionService institutionService;

  @Mock
  private InstitutionRepository institutionRepository;

  @Test
  void findInstitutions_all() {
    // given
    UUID uuid = UUID.randomUUID();
    InstitutionEntity institutionEntity = new InstitutionEntity();
    institutionEntity.setName("Jurpn");
    institutionEntity.setType("jurpn");
    institutionEntity.setId(uuid);
    RegionEntity regionEntity = new RegionEntity();
    regionEntity.setCode("AA");
    regionEntity.setId(uuid);
    institutionEntity.setRegions(Set.of(regionEntity));
    given(institutionRepository.findAll(any(Pageable.class))).willReturn(
      new PageImpl<>(List.of(institutionEntity))
    );

    // when
    var institutions = institutionService.findInstitutions(
      new InstitutionQuery(null, new QueryOptions(0, 10, "name", Sort.Direction.ASC, true))
    );

    // then
    assertThat(institutions.content()).contains(
      new Institution(
        uuid,
        "Jurpn",
        null,
        InstitutionType.LEGAL_ENTITY,
        List.of(new Region(uuid, "AA", null))
      )
    );
  }

  @Test
  void findInstitutions_something() {
    // given
    UUID uuid = UUID.randomUUID();
    InstitutionEntity institutionEntity = new InstitutionEntity();
    institutionEntity.setName("Organ");
    institutionEntity.setType("organ");
    institutionEntity.setId(uuid);
    given(
      institutionRepository.findByNameContainingIgnoreCase(eq("something"), any(Pageable.class))
    ).willReturn(new PageImpl<>(List.of(institutionEntity)));

    // when
    var institutions = institutionService.findInstitutions(
      new InstitutionQuery("something", new QueryOptions(0, 10, "name", Sort.Direction.ASC, true))
    );

    // then
    assertThat(institutions.content()).contains(
      new Institution(uuid, "Organ", null, InstitutionType.INSTITUTION, List.of())
    );
  }
}
