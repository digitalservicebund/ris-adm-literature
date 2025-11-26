package de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class DocumentTypeServiceTest {

  @InjectMocks
  private DocumentTypeService documentTypeService;

  @Mock
  private DocumentTypeRepository documentTypeRepository;

  @Test
  void findDocumentTypes_all() {
    // given
    DocumentTypeEntity documentTypeEntity = new DocumentTypeEntity();
    documentTypeEntity.setAbbreviation("VR");
    documentTypeEntity.setName("Verwaltungsregelung");
    documentTypeEntity.setDocumentCategory(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    //noinspection unchecked
    given(documentTypeRepository.findAll(any(Example.class), any(Pageable.class))).willReturn(
      new PageImpl<>(List.of(documentTypeEntity))
    );

    // when
    var documentTypes = documentTypeService.findDocumentTypes(
      new DocumentTypeQuery(
        null,
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
        new QueryOptions(0, 10, "name", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(documentTypes.content()).contains(new DocumentType("VR", "Verwaltungsregelung"));
  }

  @Test
  void findDocumentTypes_something() {
    // given
    DocumentTypeEntity documentTypeEntity = new DocumentTypeEntity();
    documentTypeEntity.setAbbreviation("VR");
    documentTypeEntity.setName("Verwaltungsregelung");
    documentTypeEntity.setDocumentCategory(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);
    given(
      documentTypeRepository.findByDocumentCategoryAndAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        eq(DocumentCategory.VERWALTUNGSVORSCHRIFTEN),
        eq("something"),
        eq("something"),
        any(Pageable.class)
      )
    ).willReturn(new PageImpl<>(List.of(documentTypeEntity)));

    // when
    var documentTypes = documentTypeService.findDocumentTypes(
      new DocumentTypeQuery(
        "something",
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
        new QueryOptions(0, 10, "name", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(documentTypes.content()).contains(new DocumentType("VR", "Verwaltungsregelung"));
  }
}
