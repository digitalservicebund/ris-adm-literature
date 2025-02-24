package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import java.text.DecimalFormat;
import java.time.Year;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DocumentNumber {

  private static final String VALID_DOCUMENT_NUMBER_PATTERN = "KSNR\\d{10}";
  private static final String DOCUMENT_NUMBER_PREFIX = "KSNR";
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("000000");

  private final Year year;
  private final String lastestDocumentNumber;

  public String create() {
    String prefix = DOCUMENT_NUMBER_PREFIX + year.getValue();
    int number = 0;
    if (lastestDocumentNumber != null) {
      if (!lastestDocumentNumber.matches(VALID_DOCUMENT_NUMBER_PATTERN)) {
        throw new IllegalArgumentException(
          "Invalid last document number: " + lastestDocumentNumber
        );
      }
      number = Integer.parseInt(lastestDocumentNumber.substring(prefix.length()));
    }
    return prefix + DECIMAL_FORMAT.format(++number);
  }
}
