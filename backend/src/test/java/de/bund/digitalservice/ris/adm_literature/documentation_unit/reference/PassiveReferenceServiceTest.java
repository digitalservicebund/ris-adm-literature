package de.bund.digitalservice.ris.adm_literature.documentation_unit.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
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
  private RefViewActiveReferenceSliAdmRepository refViewActiveReferenceSliAdmRepository;

  @Test
  void findAll_adm() {
    // given
    UUID[] sources = new UUID[] { UUID.randomUUID(), UUID.randomUUID() };
    UUID target = UUID.randomUUID();
    given(refViewActiveReferenceSliAdmRepository.findAll()).willReturn(
      List.of(
        createActiveReferenceSliAdm(sources[0], target),
        createActiveReferenceSliAdm(sources[1], target)
      )
    );

    // when
    List<PassiveReference> passiveReferences = passiveReferenceService.findAll(
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );

    // then
    assertThat(passiveReferences)
      .hasSize(2)
      .extracting(
        pr -> pr.target().documentationUnitId(),
        pr -> pr.referencedBy().documentationUnitId()
      )
      .containsExactly(Tuple.tuple(target, sources[0]), Tuple.tuple(target, sources[1]));
  }

  @ParameterizedTest
  @EnumSource(
    value = DocumentCategory.class,
    names = { "LITERATUR_SELBSTAENDIG", "LITERATUR_UNSELBSTAENDIG", "LITERATUR" }
  )
  void findAll_unsupported(DocumentCategory documentCategory) {
    // given

    // when
    List<PassiveReference> passiveReferences = passiveReferenceService.findAll(documentCategory);

    // then
    assertThat(passiveReferences).isEmpty();
  }

  @Test
  void findByTarget() {
    // given
    UUID[] sources = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID() };
    UUID target = UUID.randomUUID();
    given(
      refViewActiveReferenceSliAdmRepository.findByTargetDocumentationUnitId(target)
    ).willReturn(
      List.of(
        createActiveReferenceSliAdm(sources[0], target),
        createActiveReferenceSliAdm(sources[1], target),
        createActiveReferenceSliAdm(sources[2], target)
      )
    );

    // when
    List<DocumentReference> referencedBy = passiveReferenceService.findByTarget(
      target,
      DocumentCategory.VERWALTUNGSVORSCHRIFTEN
    );

    // then
    assertThat(referencedBy)
      .hasSize(3)
      .extracting(DocumentReference::documentationUnitId)
      .containsExactly(sources);
  }

  @ParameterizedTest
  @EnumSource(
    value = DocumentCategory.class,
    names = { "LITERATUR_SELBSTAENDIG", "LITERATUR_UNSELBSTAENDIG", "LITERATUR" }
  )
  void findByTarget_unsupported(DocumentCategory documentCategory) {
    // given

    // when
    List<DocumentReference> referencedBy = passiveReferenceService.findByTarget(
      UUID.randomUUID(),
      documentCategory
    );

    // then
    assertThat(referencedBy).isEmpty();
  }

  private RefViewActiveReferenceSliAdmEntity createActiveReferenceSliAdm(UUID source, UUID target) {
    RefViewActiveReferenceSliAdmEntity refViewActiveReferenceSliAdmEntity =
      new RefViewActiveReferenceSliAdmEntity();
    refViewActiveReferenceSliAdmEntity.setId(UUID.randomUUID());
    refViewActiveReferenceSliAdmEntity.setSourceDocumentationUnitId(source);
    refViewActiveReferenceSliAdmEntity.setTargetDocumentationUnitId(target);
    return refViewActiveReferenceSliAdmEntity;
  }
}
