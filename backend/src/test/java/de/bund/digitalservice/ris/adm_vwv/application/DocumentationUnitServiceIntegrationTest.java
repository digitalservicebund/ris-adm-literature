package de.bund.digitalservice.ris.adm_vwv.application;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.TestcontainersConfiguration;

import java.util.Optional;

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
  void find() {
    // given
    DocumentationUnit documentationUnit = documentationUnitService.create();
    String documentNumber = documentationUnit.documentNumber();

    // when
    Optional<DocumentationUnit> actual = documentationUnitService.findByDocumentNumber(documentNumber);

    // then
    assertThat(actual)
      .isPresent()
      .hasValueSatisfying(actualDocumentationUnit ->
        assertThat(actualDocumentationUnit.id()).isEqualTo(documentationUnit.id()));
  }

  @Test
  void create() {
    // given

    // when
    DocumentationUnit documentationUnit = documentationUnitService.create();

    // then
    assertThat(documentationUnit)
      .isNotNull()
      .extracting(DocumentationUnit::documentNumber)
      .satisfies(documentNumber -> assertThat(documentNumber).startsWith("KSNR"));
  }

  @Test
  void update() {
    // given
    DocumentationUnit documentationUnit = documentationUnitService.create();

    // when
    Optional<DocumentationUnit> updated = documentationUnitService.update(
      documentationUnit.documentNumber(),
      "{\"test\":\"content\"}"
    );

    // then
    assertThat(updated)
      .isPresent()
      .hasValueSatisfying(dun -> assertThat(dun.json()).isEqualTo("{\"test\":\"content\"}"));
  }

  @Test
  void update_notFound() {
    // given
    String documentNumber = "KSNR000000001";

    // when
    Optional<DocumentationUnit> updated = documentationUnitService.update(documentNumber, "{}");

    // then
    assertThat(updated).isEmpty();
  }
}
