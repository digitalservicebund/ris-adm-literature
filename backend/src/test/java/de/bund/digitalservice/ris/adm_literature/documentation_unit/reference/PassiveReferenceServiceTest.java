package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
  @Disabled("Until implementing RISDEV-10990")
  void findAll_adm() {
    // given
    given(admPassiveReferenceRepository.findAll()).willReturn(
      List.of(
        createAdmPassiveReference("KSNR20260000001", "KSLS20260000001"),
        createAdmPassiveReference("KSNR20260000001", "KSLS20260000002")
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
        Tuple.tuple("KSNR20260000001", "KSLS20260000001"),
        Tuple.tuple("KSNR20260000001", "KSLS20260000002")
      );
  }

  @ParameterizedTest
  @EnumSource(
    value = DocumentCategory.class,
    names = {
      "LITERATUR_SELBSTAENDIG", "LITERATUR_UNSELBSTAENDIG", "LITERATUR", "VERWALTUNGSVORSCHRIFTEN",
    }
  )
  void findAll_unsupported(DocumentCategory documentCategory) {
    // given

    // when
    List<PassiveReference> passiveReferences = passiveReferenceService.findAll(documentCategory);

    // then
    assertThat(passiveReferences).isEmpty();
  }

  @Test
  @Disabled("Until implementing RISDEV-10990")
  void findByDocumentNumber() {
    // given
    given(admPassiveReferenceRepository.findByTargetDocumentNumber("KSNR20260000333")).willReturn(
      List.of(
        createAdmPassiveReference("KSNR20260000333", "KSLS20260000001"),
        createAdmPassiveReference("KSNR20260000333", "KSLS20260000002"),
        createAdmPassiveReference("KSNR20260000333", "KSLS20260000003")
      )
    );

    // when
    List<DocumentReference> referencedBy = passiveReferenceService.findByDocumentNumber(
      "KSNR20260000333",
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );

    // then
    assertThat(referencedBy)
      .hasSize(3)
      .extracting(DocumentReference::documentNumber)
      .containsExactly("KSLS20260000001", "KSLS20260000002", "KSLS20260000003");
  }

  @ParameterizedTest
  @EnumSource(
    value = DocumentCategory.class,
    names = { "LITERATUR_SELBSTAENDIG", "LITERATUR_UNSELBSTAENDIG", "LITERATUR" }
  )
  void findByDocumentNumber_unsupported(DocumentCategory documentCategory) {
    // given

    // when
    List<DocumentReference> referencedBy = passiveReferenceService.findByDocumentNumber(
      "documentNumber",
      documentCategory
    );

    // then
    assertThat(referencedBy).isEmpty();
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
