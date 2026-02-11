package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.AdmIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.RefViewAdmEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.reference.RefViewAdmRepository;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AdmReferenceServiceTest {

  @InjectMocks
  private AdmReferenceService admReferenceService;

  @Mock
  private RefViewAdmRepository refViewAdmRepository;

  @Test
  void findAktivzitierungen() {
    // given
    RefViewAdmEntity entityWithIndex = createDocumentationUnitEntity();
    RefViewAdmEntity entityWithoutIndex = new RefViewAdmEntity();
    entityWithoutIndex.setDocumentNumber("ADM-002");
    given(
      refViewAdmRepository.findAll(any(AdmReferenceSpecification.class), any(Pageable.class))
    ).willReturn(new PageImpl<>(List.of(entityWithIndex, entityWithoutIndex)));
    AdmAktivzitierungQuery query = new AdmAktivzitierungQuery(
      "ADM",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      new QueryOptions(0, 10, "documentNumber", Sort.Direction.DESC, true)
    );

    // when
    var result = admReferenceService.findAktivzitierungen(query);

    // then
    assertThat(result.content())
      .hasSize(2)
      .extracting(
        AdmAktivzitierungResult::documentNumber,
        AdmAktivzitierungResult::inkrafttretedatum,
        AdmAktivzitierungResult::langueberschrift,
        AdmAktivzitierungResult::dokumenttyp,
        AdmAktivzitierungResult::normgeberList,
        AdmAktivzitierungResult::fundstellen,
        AdmAktivzitierungResult::aktenzeichenList,
        AdmAktivzitierungResult::zitierdaten
      )
      .containsExactly(
        Tuple.tuple(
          "ADM-001",
          "2025-01-01",
          "Administrative Title",
          "VV",
          List.of("BMJ"),
          List.of("BGBI I S. 1"),
          List.of("AZ-123"),
          List.of("2025-01-01")
        ),
        Tuple.tuple("ADM-002", null, null, null, null, null, null, null)
      );
  }

  private static @NonNull RefViewAdmEntity createDocumentationUnitEntity() {
    RefViewAdmEntity refViewAdmEntity = new RefViewAdmEntity();
    refViewAdmEntity.setDocumentNumber("ADM-001");
    var admIndex = new AdmIndex();
    admIndex.setInkrafttretedatum("2025-01-01");
    admIndex.setLangueberschrift("Administrative Title");
    admIndex.setDokumenttyp("VV");
    admIndex.setNormgeberList(List.of("BMJ"));
    refViewAdmEntity.setFundstellen(List.of("BGBI I S. 1"));
    admIndex.setZitierdaten(List.of("§ 5"));
    admIndex.setAktenzeichenList(List.of("AZ-123"));
    admIndex.setZitierdaten(List.of("2025-01-01"));
    refViewAdmEntity.setAdmIndex(admIndex);
    return refViewAdmEntity;
  }
}
