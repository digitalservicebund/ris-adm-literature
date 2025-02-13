package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    given(documentationUnitRepository.findByDocumentNumber("KSNR000000002"))
      .willReturn(Optional.of(documentationUnitEntity));

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber("KSNR000000002");

    // then
    assertThat(documentationUnit)
      .hasValueSatisfying(actual -> assertThat(actual.json()).isEqualTo("\"abc\":false"));
  }

  @Test
  void findByDocumentNumber_noFound() {
    // given
    given(documentationUnitRepository.findByDocumentNumber("KSNR000000002"))
      .willReturn(Optional.empty());

    // when
    Optional<DocumentationUnit> documentationUnit =
      documentationUnitPersistenceService.findByDocumentNumber("KSNR000000002");

    // then
    assertThat(documentationUnit).isEmpty();
  }
}
