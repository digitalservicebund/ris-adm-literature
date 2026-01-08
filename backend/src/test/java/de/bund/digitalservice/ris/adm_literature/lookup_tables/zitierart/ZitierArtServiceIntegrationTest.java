package de.bund.digitalservice.ris.adm_literature.lookup_tables.zitierart;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.page.Page;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ZitierArtServiceIntegrationTest {

  @Autowired
  private ZitierArtService zitierArtService;

  @Test
  @DisplayName("Find Zitieraten filtered by document category 'Verwaltungvorschriften")
  void findZitierArten_adm() {
    // given

    // when
    Page<ZitierArt> zitierArten = zitierArtService.findZitierArten(
      new ZitierArtQuery(
        null,
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
        null,
        new QueryOptions(0, 10, "abbreviation", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(zitierArten.content())
      .hasSize(4)
      .extracting(ZitierArt::abbreviation)
      .containsExactly("Abgrenzung", "Ablehnung", "Änderung", "Übernahme");
  }

  @Test
  @DisplayName("Find Zitieraten filtered by document category 'Literatur unselbständig")
  void findZitierArten_uli() {
    // given

    // when
    Page<ZitierArt> zitierArten = zitierArtService.findZitierArten(
      new ZitierArtQuery(
        null,
        DocumentCategory.LITERATUR,
        DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
        new QueryOptions(0, 10, "abbreviation", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(zitierArten.content())
      .hasSize(4)
      .extracting(ZitierArt::abbreviation)
      .containsExactly("Ablehnung", "Vergleiche", "XX", "Zustimmung");
  }
}
