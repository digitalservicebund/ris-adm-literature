package de.bund.digitalservice.ris.adm_vwv.application;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DocumentationUnitServiceIntegrationTest {

  @Autowired
  private DocumentationUnitService documentationUnitService;

  @Test
  void create() {
    // given

    // when
    DocumentationUnit documentationUnit = documentationUnitService.create();

    // then
    assertThat(documentationUnit)
      .isNotNull()
      .extracting(DocumentationUnit::documentNumber)
      .satisfies(dun -> assertThat(dun).startsWith("KSNR"));
  }
}
