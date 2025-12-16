package de.bund.digitalservice.ris.adm_literature.documentation_unit.adm;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical.LegalPeriodical;
import de.bund.digitalservice.ris.adm_literature.lookup_tables.legal_periodical.TestLegalPeriodical;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FundstelleTest {

  private static Stream<Arguments> fundstellen() {
    return Stream.of(
      // zitatstelle, legalPeriodical, ambiguousPeriodikum, expected formatted string
      Arguments.of("Seite 2", TestLegalPeriodical.create("VJP"), null, "VJP Seite 2"),
      Arguments.of("Nr. 117 (1977)", TestLegalPeriodical.create("AA"), null, "AA Nr. 117 (1977)"),
      Arguments.of("1978 (Band 3)", null, "Die Beiträge", "Die Beiträge 1978 (Band 3)")
    );
  }

  @ParameterizedTest
  @MethodSource("fundstellen")
  void toFormattedString(
    String zitatstelle,
    LegalPeriodical legalPeriodical,
    String ambiguousPeriodikum,
    String expected
  ) {
    // given
    Fundstelle fundstelle = new Fundstelle(
      UUID.randomUUID(),
      zitatstelle,
      legalPeriodical,
      ambiguousPeriodikum
    );

    // when
    String formattedString = fundstelle.toFormattedString();

    // then
    assertThat(formattedString).isEqualTo(expected);
  }
}
