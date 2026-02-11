package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.adm.AdmDocumentionUnitSpecification;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.AdmIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.DocumentationUnitIndexEntity;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.indexing.LiteratureIndex;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.SliDocumentationUnitSpecification;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitQuery;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.UliDocumentationUnitSpecification;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.notes.NoteService;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type.DocumentTypeService;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DocumentationUnitPersistenceServiceTest {

  @InjectMocks
  private DocumentationUnitPersistenceService documentationUnitPersistenceService;

  @Mock
  private DocumentationUnitRepository documentationUnitRepository;

  @Mock
  private DocumentTypeService documentTypeService;

  @Mock
  private NoteService noteService;

  @Spy
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void findByDocumentNumber() {
    // given
    DocumentationUnitEntity documentationUnitEntity = new DocumentationUnitEntity();
    documentationUnitEntity.setDocumentNumber("KSNR000000002");
    documentationUnitEntity.setJson("{\"abc\":false}");
    given(documentationUnitRepository.findByDocumentNumber("KSNR000000002")).willReturn(
      Optional.of(documentationUnitEntity)
    );

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber("KSNR000000002");

    // then
    assertThat(documentationUnit).hasValueSatisfying(actual ->
      assertThat(actual.json()).isEqualTo("{\"abc\":false}")
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
  void findAdmDocumentationUnitOverviewElements() {
    // given
    DocumentationUnitEntity entityWithIndex = new DocumentationUnitEntity();
    entityWithIndex.setId(UUID.randomUUID());
    entityWithIndex.setDocumentNumber("DOC-001");
    DocumentationUnitIndexEntity index = new DocumentationUnitIndexEntity();
    index.setFundstellenCombined("Citation 1");
    index.setFundstellen(List.of("Citation 1"));
    AdmIndex admIndex = index.getAdmIndex();
    admIndex.setLangueberschrift("Title 1");
    admIndex.setZitierdatenCombined("2023-01-01");
    admIndex.setZitierdaten(List.of("2023-01-01"));
    entityWithIndex.setDocumentationUnitIndex(index);

    DocumentationUnitEntity entityWithoutIndex = new DocumentationUnitEntity();
    entityWithoutIndex.setId(UUID.randomUUID());
    entityWithoutIndex.setDocumentNumber("DOC-002");
    entityWithoutIndex.setDocumentationUnitIndex(null);

    Page<DocumentationUnitEntity> pageOfEntities = new PageImpl<>(
      List.of(entityWithIndex, entityWithoutIndex)
    );

    given(
      documentationUnitRepository.findAll(
        any(AdmDocumentionUnitSpecification.class),
        any(Pageable.class)
      )
    ).willReturn(pageOfEntities);

    // when
    de.bund.digitalservice.ris.adm_literature.page.Page<
      AdmDocumentationUnitOverviewElement
    > result = documentationUnitPersistenceService.findAdmDocumentationUnitOverviewElements(
      new AdmDocumentationUnitQuery(
        null,
        null,
        null,
        null,
        new QueryOptions(0, 10, "id", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(result.content())
      .hasSize(2)
      .extracting(
        AdmDocumentationUnitOverviewElement::documentNumber,
        AdmDocumentationUnitOverviewElement::langueberschrift,
        AdmDocumentationUnitOverviewElement::zitierdaten,
        AdmDocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly(
        Tuple.tuple("DOC-001", "Title 1", List.of("2023-01-01"), List.of("Citation 1")),
        Tuple.tuple("DOC-002", null, emptyList(), emptyList())
      );

    // Assert transformation for the entity WITH an index
    AdmDocumentationUnitOverviewElement elementWithIndex = result.content().getFirst();
    assertThat(elementWithIndex.id()).isEqualTo(entityWithIndex.getId());
    assertThat(elementWithIndex.documentNumber()).isEqualTo("DOC-001");
    assertThat(elementWithIndex.langueberschrift()).isEqualTo("Title 1");
    assertThat(elementWithIndex.zitierdaten()).containsExactly("2023-01-01");
    assertThat(elementWithIndex.fundstellen()).containsExactly("Citation 1");

    // Assert transformation for the entity WITHOUT an index
    AdmDocumentationUnitOverviewElement elementWithoutIndex = result.content().get(1);
    assertThat(elementWithoutIndex.id()).isEqualTo(entityWithoutIndex.getId());
    assertThat(elementWithoutIndex.documentNumber()).isEqualTo("DOC-002");
    assertThat(elementWithoutIndex.langueberschrift()).isNull();
    assertThat(elementWithoutIndex.zitierdaten()).isEmpty();
    assertThat(elementWithoutIndex.fundstellen()).isEmpty();
  }

  @Test
  void findLiteratureDocumentationUnitOverviewElements() {
    // given
    DocumentationUnitEntity entityWithIndex = new DocumentationUnitEntity();
    entityWithIndex.setId(UUID.randomUUID());
    entityWithIndex.setDocumentNumber("LIT-001");

    DocumentationUnitIndexEntity index = new DocumentationUnitIndexEntity();
    LiteratureIndex literatureIndex = index.getLiteratureIndex();
    literatureIndex.setVeroeffentlichungsjahr("2024");
    literatureIndex.setTitel("Literature Title");
    literatureIndex.setDokumenttypen(List.of("Ebs", "Dis"));
    literatureIndex.setDokumenttypenCombined("Ebs Dis");
    literatureIndex.setVerfasserList(List.of("Doe, John", "Smith, Jane"));
    literatureIndex.setVerfasserListCombined("Doe, John Smith, Jane");
    entityWithIndex.setDocumentationUnitIndex(index);

    DocumentationUnitEntity entityWithoutIndex = new DocumentationUnitEntity();
    entityWithoutIndex.setId(UUID.randomUUID());
    entityWithoutIndex.setDocumentNumber("LIT-002");
    entityWithoutIndex.setDocumentationUnitIndex(null);

    Page<DocumentationUnitEntity> pageOfEntities = new PageImpl<>(
      List.of(entityWithIndex, entityWithoutIndex)
    );

    given(
      documentationUnitRepository.findAll(
        any(SliDocumentationUnitSpecification.class),
        any(Pageable.class)
      )
    ).willReturn(pageOfEntities);

    // when
    var result = documentationUnitPersistenceService.findSliDocumentationUnitOverviewElements(
      new SliDocumentationUnitQuery(
        null,
        null,
        null,
        null,
        null,
        new QueryOptions(0, 10, "documentNumber", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(result.content())
      .hasSize(2)
      .extracting(
        SliDocumentationUnitOverviewElement::documentNumber,
        SliDocumentationUnitOverviewElement::titel,
        SliDocumentationUnitOverviewElement::veroeffentlichungsjahr
      )
      .containsExactly(
        Tuple.tuple("LIT-001", "Literature Title", "2024"),
        Tuple.tuple("LIT-002", null, null)
      );

    SliDocumentationUnitOverviewElement elementWithIndex = result.content().getFirst();
    assertThat(elementWithIndex.dokumenttypen()).containsExactly("Ebs", "Dis");
    assertThat(elementWithIndex.verfasser()).containsExactly("Doe, John", "Smith, Jane");

    SliDocumentationUnitOverviewElement elementWithoutIndex = result.content().get(1);
    assertThat(elementWithoutIndex.dokumenttypen()).isEmpty();
    assertThat(elementWithoutIndex.verfasser()).isEmpty();
  }

  @Test
  void findUliDocumentationUnitOverviewElements() {
    // given
    DocumentationUnitEntity entityWithIndex = new DocumentationUnitEntity();
    entityWithIndex.setId(UUID.randomUUID());
    entityWithIndex.setDocumentNumber("ULI-001");

    DocumentationUnitIndexEntity index = new DocumentationUnitIndexEntity();
    index.setFundstellenCombined("NJW 2024, 123");

    LiteratureIndex literatureIndex = index.getLiteratureIndex();
    literatureIndex.setDokumenttypen(List.of("Aufsatz"));
    literatureIndex.setDokumenttypenCombined("Aufsatz");
    literatureIndex.setVerfasserList(List.of("Müller, Martin"));
    literatureIndex.setVerfasserListCombined("Müller, Martin");
    entityWithIndex.setDocumentationUnitIndex(index);

    DocumentationUnitEntity entityWithoutIndex = new DocumentationUnitEntity();
    entityWithoutIndex.setId(UUID.randomUUID());
    entityWithoutIndex.setDocumentNumber("ULI-002");
    entityWithoutIndex.setDocumentationUnitIndex(null);

    Page<DocumentationUnitEntity> pageOfEntities = new PageImpl<>(
      List.of(entityWithIndex, entityWithoutIndex)
    );

    given(
      documentationUnitRepository.findAll(
        any(UliDocumentationUnitSpecification.class),
        any(Pageable.class)
      )
    ).willReturn(pageOfEntities);

    // when
    var result = documentationUnitPersistenceService.findUliDocumentationUnitOverviewElements(
      new UliDocumentationUnitQuery(
        null,
        null,
        null,
        null,
        null,
        new QueryOptions(0, 10, "documentNumber", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(result.content())
      .hasSize(2)
      .extracting(
        UliDocumentationUnitOverviewElement::documentNumber,
        UliDocumentationUnitOverviewElement::fundstellen
      )
      .containsExactly(Tuple.tuple("ULI-001", "NJW 2024, 123"), Tuple.tuple("ULI-002", null));

    UliDocumentationUnitOverviewElement elementWithIndex = result.content().getFirst();
    assertThat(elementWithIndex.dokumenttypen()).containsExactly("Aufsatz");
    assertThat(elementWithIndex.verfasser()).containsExactly("Müller, Martin");

    UliDocumentationUnitOverviewElement elementWithoutIndex = result.content().get(1);
    assertThat(elementWithoutIndex.dokumenttypen()).isEmpty();
    assertThat(elementWithoutIndex.verfasser()).isEmpty();
  }
}
