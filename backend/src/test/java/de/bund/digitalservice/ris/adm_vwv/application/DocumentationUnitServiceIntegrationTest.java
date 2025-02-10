package de.bund.digitalservice.ris.adm_vwv.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
