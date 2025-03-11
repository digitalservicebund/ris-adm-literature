package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class LookupTablesPersistenceServiceTest {

  @InjectMocks
  private LookupTablesPersistenceService lookupTablesPersistenceService;

  @Mock
  private DocumentTypesRepository documentTypesRepository;

  @Test
  void findBySearchQuery_all() {
    // given
    DocumentTypeEntity documentTypeEntity = new DocumentTypeEntity();
    documentTypeEntity.setAbbreviation("VR");
    documentTypeEntity.setName("Verwaltungsregelung");
    given(documentTypesRepository.findAll()).willReturn(List.of(documentTypeEntity));

    // when
    List<DocumentType> documentTypes = lookupTablesPersistenceService.findBySearchQuery(null);

    // then
    assertThat(documentTypes).contains(new DocumentType("VR", "Verwaltungsregelung"));
  }

  @Test
  void findBySearchQuery_something() {
    // given
    DocumentTypeEntity documentTypeEntity = new DocumentTypeEntity();
    documentTypeEntity.setAbbreviation("VR");
    documentTypeEntity.setName("Verwaltungsregelung");
    given(
      documentTypesRepository.findByAbbreviationContainingIgnoreCaseOrNameContainingIgnoreCase(
        "something",
        "something"
      )
    ).willReturn(List.of(documentTypeEntity));

    // when
    List<DocumentType> documentTypes = lookupTablesPersistenceService.findBySearchQuery(
      "something"
    );

    // then
    assertThat(documentTypes).contains(new DocumentType("VR", "Verwaltungsregelung"));
  }
}
