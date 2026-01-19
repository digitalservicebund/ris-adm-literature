package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
class PassiveReferenceServiceTest {

  @InjectMocks
  private PassiveReferenceService passiveReferenceService;

  @Mock
  private AdmPassiveReferenceRepository admPassiveReferenceRepository;

  @Test
  void findAll_adm() {
    // given
    given(admPassiveReferenceRepository.findAll()).willReturn(
      List.of(
        createAdmPassiveReference("KSLS20260000001", "KSNR20260000001"),
        createAdmPassiveReference("KSLS20260000001", "KSNR20260000002")
      )
    );

    // when
    List<PassiveReference> passiveReferences = passiveReferenceService.findAll(
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );

    // then
    assertThat(passiveReferences)
      .hasSize(2)
      .extracting(pr -> pr.target().documentNumber(), pr -> pr.referencedBy().documentNumber())
      .containsExactly(
        Tuple.tuple("KSLS20260000001", "KSNR20260000001"),
        Tuple.tuple("KSLS20260000001", "KSNR20260000002")
      );
  }

  @Test
  void findAll_sli() {
    // given

    // when
    List<PassiveReference> passiveReferences = passiveReferenceService.findAll(
      DocumentCategory.LITERATUR_SELBSTAENDIG
    );

    // then
    assertThat(passiveReferences).isEmpty();
  }

  private AdmPassiveReferenceEntity createAdmPassiveReference(String target, String source) {
    AdmPassiveReferenceEntity admPassiveReferenceEntity = new AdmPassiveReferenceEntity();
    admPassiveReferenceEntity.setActiveReferenceId(UUID.randomUUID());
    admPassiveReferenceEntity.setSourceDocumentNumber(source);
    admPassiveReferenceEntity.setSourceDocumentCategory(DocumentCategory.LITERATUR_SELBSTAENDIG);
    admPassiveReferenceEntity.setTargetDocumentNumber(target);
    return admPassiveReferenceEntity;
  }
}
