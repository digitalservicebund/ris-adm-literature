package de.bund.digitalservice.ris.adm_vwv.application.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SlugTest {

  private static Stream<Arguments> values() {
    return Stream.of(
      // value, expected
      Arguments.of("Verband Deutscher Badeärzte", "verband-deutscher-badeaerzte"),
      Arguments.of("BEWirtschaftsSen", "bewirtschaftssen"),
      Arguments.of("KK-Spitzenverbände", "kk-spitzenverbaende"),
      Arguments.of(
        "Arbeitsgemeinschaft gemäß § 22 ZÄErsKVtr",
        "arbeitsgemeinschaft-gemaess-22-zaeerskvtr"
      ),
      Arguments.of(
        "Ständige Gebührenkommission Ärzte/Berufsgenossenschaften",
        "staendige-gebuehrenkommission-aerzte-berufsgenossenschaften"
      ),
      Arguments.of("Erfundene Vorschrift mit Ákzent & §§/§/§§", "erfundene-vorschrift-mit-kzent")
    );
  }

  @ParameterizedTest
  @MethodSource("values")
  void testToString(String value, String expected) {
    // given
    Slug slug = new Slug(value);

    // when
    String actual = slug.toString();

    // then
    assertThat(actual).isEqualTo(expected);
  }
}
