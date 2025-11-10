package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_vwv.application.converter.business.TestUliDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.UliDocumentationUnitContent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UliLdmlConverterStrategyIntegrationTest {

  @Autowired
  private UliLdmlConverterStrategy uliLdmlConverterStrategy;

  @Test
  void convertToLdml() {
    // given
    UliDocumentationUnitContent uliDocumentationUnitContent =
      TestUliDocumentationUnitContent.create("KSLU00000011", "2025");

    // when
    String xml = uliLdmlConverterStrategy.convertToLdml(uliDocumentationUnitContent, null);

    // then
    assertThat(xml).contains(
      """
      <akn:FRBRalias name="Dokumentnummer" value="KSLU00000011"/>"""
    );
  }
}
