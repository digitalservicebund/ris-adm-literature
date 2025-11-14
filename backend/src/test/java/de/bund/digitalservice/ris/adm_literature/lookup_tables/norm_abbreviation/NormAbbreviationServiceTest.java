package de.bund.digitalservice.ris.adm_literature.lookup_tables.norm_abbreviation;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class NormAbbreviationServiceTest {

  @InjectMocks
  private NormAbbreviationService normAbbreviationService;

  @Test
  void findNormAbbreviations_all() {
    // given

    // when
    var abbreviations = normAbbreviationService.findNormAbbreviations(
      new NormAbbreviationQuery(
        null,
        new QueryOptions(0, 10, "abbreviation", Sort.Direction.ASC, true)
      )
    );

    // then
    assertThat(abbreviations.content())
      .extracting(NormAbbreviation::abbreviation, NormAbbreviation::officialLongTitle)
      .containsExactly(
        Tuple.tuple("SGB 5", "Sozialgesetzbuch (SGB) Fünftes Buch (V)"),
        Tuple.tuple(
          "KVLG",
          "Gesetz zur Weiterentwicklung des Rechts der gesetzlichen Krankenversicherung"
        )
      );
  }
}
