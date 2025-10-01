package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.time.Year;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentNumberTest {

  /**
   * Dynamically generates test cases for every DocumentationOffice.
   * For each office, it creates two tests: one for the first document of the
   * year and one for incrementing an existing document.
   */
  private static Stream<Arguments> documentNumberProvider() {
    final Year year = Year.of(2025);

    return Arrays.stream(DocumentationOffice.values()).flatMap(office -> {
      String prefix = office.getPrefix();
      String firstDoc = prefix + year.getValue() + "000001";
      String secondDoc = prefix + year.getValue() + "000002";
      return Stream.of(
        // prefix, year, latestNumber, expected
        Arguments.of(prefix, year, null, firstDoc),
        Arguments.of(prefix, year, firstDoc, secondDoc)
      );
    });
  }

  @ParameterizedTest
  @MethodSource("documentNumberProvider")
  void create(String prefix, Year year, String latestNumber, String expected) {
    // given
    DocumentNumber documentNumber = new DocumentNumber(prefix, year, latestNumber);

    // when
    String actual = documentNumber.create();

    // then
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Dynamically generates failure test cases for every DocumentationOffice.
   * For each office, it tests various invalid formats for the latestNumber.
   */
  private static Stream<Arguments> failingDocumentNumberProvider() {
    final Year year = Year.of(2025);
    final Year wrongYear = Year.of(2026);

    return Arrays.stream(DocumentationOffice.values()).flatMap(office -> {
      String prefix = office.getPrefix();
      return Stream.of(
        // prefix, year, latestNumber
        Arguments.of(prefix, year, "XXXX" + year.getValue() + "000001"), // Mismatched prefix
        Arguments.of(prefix, year, prefix + wrongYear.getValue() + "000001"), // Mismatched year
        Arguments.of(prefix, year, prefix + year.getValue() + "-000001") // Invalid format
      );
    });
  }

  @ParameterizedTest
  @MethodSource("failingDocumentNumberProvider")
  void create_failures(String prefix, Year year, String latestNumber) {
    // given
    DocumentNumber documentNumber = new DocumentNumber(prefix, year, latestNumber);

    // when
    Exception exception = catchException(documentNumber::create);

    // then
    assertThat(exception)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Invalid last document number");
  }
}
