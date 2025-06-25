package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class DocumentationUnitPersistenceServiceTest {

  @InjectMocks
  private DocumentationUnitPersistenceService documentationUnitPersistenceService;

  @Mock
  private DocumentationUnitRepository documentationUnitRepository;

  @Test
  void findByDocumentNumber() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR000000002");
    documentationUnitEntity.setJson("\"abc\":false");
    given(documentationUnitRepository.findByDocumentNumber("KSNR000000002")).willReturn(
      Optional.of(documentationUnitEntity)
    );

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber("KSNR000000002");

    // then
    assertThat(documentationUnit).hasValueSatisfying(actual ->
      assertThat(actual.json()).isEqualTo("\"abc\":false")
    );
  }

  @Test
  void findByDocumentNumber_notFound() {
    // given
    given(documentationUnitRepository.findByDocumentNumber("KSNR000000002")).willReturn(
      Optional.empty()
    );

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber("KSNR000000002");

    // then
    assertThat(documentationUnit).isEmpty();
  }

  @Test
  void findDocumentationUnitOverviewElements() {
    // given
    DocumentationUnitEntity entityWithIndex = new DocumentationUnitEntity();
    entityWithIndex.setId(UUID.randomUUID());
    entityWithIndex.setDocumentNumber("DOC-001");
    DocumentationUnitIndexEntity index = new DocumentationUnitIndexEntity();
    index.setLangueberschrift("Title 1");
    index.setZitierdaten("2023-01-01");
    index.setFundstellen("Citation 1");
    entityWithIndex.setDocumentationUnitIndex(index);

    DocumentationUnitEntity entityWithoutIndex = new DocumentationUnitEntity();
    entityWithoutIndex.setId(UUID.randomUUID());
    entityWithoutIndex.setDocumentNumber("DOC-002");
    entityWithoutIndex.setDocumentationUnitIndex(null);

    Page<DocumentationUnitEntity> pageOfEntities = new PageImpl<>(
      List.of(entityWithIndex, entityWithoutIndex)
    );

    given(
      documentationUnitRepository.findAll(any(DocumentUnitSpecification.class), any(Pageable.class))
    ).willReturn(pageOfEntities);

    // when
    de.bund.digitalservice.ris.adm_vwv.application.Page<DocumentationUnitOverviewElement> result =
      documentationUnitPersistenceService.findDocumentationUnitOverviewElements(
        new DocumentationUnitQuery(null, null, null, null),
        PageRequest.of(0, 10)
      );

    // then
    assertThat(result.content())
      .hasSize(2)
      .extracting(
        DocumentationUnitOverviewElement::documentNumber,
        DocumentationUnitOverviewElement::langueberschrift,
        DocumentationUnitOverviewElement::zitierdaten,
        DocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly(
        Tuple.tuple("DOC-001", "Title 1", List.of("2023-01-01"), List.of("Citation 1")),
        Tuple.tuple("DOC-002", null, emptyList(), emptyList())
      );

    // Assert transformation for the entity WITH an index
    DocumentationUnitOverviewElement elementWithIndex = result.content().getFirst();
    assertThat(elementWithIndex.id()).isEqualTo(entityWithIndex.getId());
    assertThat(elementWithIndex.documentNumber()).isEqualTo("DOC-001");
    assertThat(elementWithIndex.langueberschrift()).isEqualTo("Title 1");
    assertThat(elementWithIndex.zitierdaten()).containsExactly("2023-01-01");
    assertThat(elementWithIndex.fundstellen()).containsExactly("Citation 1");

    // Assert transformation for the entity WITHOUT an index
    DocumentationUnitOverviewElement elementWithoutIndex = result.content().get(1);
    assertThat(elementWithoutIndex.id()).isEqualTo(entityWithoutIndex.getId());
    assertThat(elementWithoutIndex.documentNumber()).isEqualTo("DOC-002");
    assertThat(elementWithoutIndex.langueberschrift()).isNull();
    assertThat(elementWithoutIndex.zitierdaten()).isEmpty();
    assertThat(elementWithoutIndex.fundstellen()).isEmpty();
  }
}
