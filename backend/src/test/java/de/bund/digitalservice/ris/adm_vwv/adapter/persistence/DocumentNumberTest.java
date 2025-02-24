package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import static org.assertj.core.api.Assertions.*;

import java.time.Year;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DocumentNumberTest {

  private static Stream<Arguments> lastDocumentNumbers() {
    return Stream.of(
      // year, lastDocumentNumber, expected
      Arguments.of(Year.of(2025), null, "KSNR2025000001"),
      Arguments.of(Year.of(2026), null, "KSNR2026000001"),
      Arguments.of(Year.of(2025), "KSNR2025000001", "KSNR2025000002")
    );
  }

  @ParameterizedTest
  @MethodSource("lastDocumentNumbers")
  void create(Year year, String lastDocumentNumber, String expected) {
    // given
    DocumentNumber documentNumber = new DocumentNumber(year, lastDocumentNumber);

    // when
    String actual = documentNumber.create();

    // then
    assertThat(actual).isEqualTo(expected);
  }

  private static Stream<Arguments> failingDocumentNumbers() {
    return Stream.of(
      // year, lastDocumentNumber
      Arguments.of(Year.of(2025), "KSNR2025-000001"),
      Arguments.of(Year.of(2026), ""),
      Arguments.of(Year.of(2025), "KSNE2025000001")
    );
  }

  @ParameterizedTest
  @MethodSource("failingDocumentNumbers")
  void create_failures(Year year, String lastDocumentNumber) {
    // given
    DocumentNumber documentNumber = new DocumentNumber(year, lastDocumentNumber);

    // when
    Exception exception = catchException(documentNumber::create);

    // then
    assertThat(exception).isInstanceOf(IllegalArgumentException.class);
  }
}
