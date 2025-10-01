package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import java.text.DecimalFormat;
import java.time.Year;
import java.util.Objects;

public class DocumentNumber {

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("000000");

  private final String prefix;
  private final Year year;
  private final String latestNumber;

  /**
   * The NEW constructor for office-specific document numbers (e.g., KALU, STLU).
   * @param prefix The office-specific prefix.
   * @param year The current year.
   * @param latestNumber The last persisted number for this series, or null.
   */
  public DocumentNumber(String prefix, Year year, String latestNumber) {
    this.prefix = Objects.requireNonNull(prefix);
    this.year = Objects.requireNonNull(year);
    this.latestNumber = latestNumber;
  }

  public String create() {
    String fullPrefix = this.prefix + this.year.getValue();
    int number = 0;

    if (latestNumber != null && !latestNumber.isEmpty()) {
      if (!latestNumber.startsWith(fullPrefix)) {
        throw new IllegalArgumentException(
          "Invalid last document number: " + latestNumber + " does not match prefix " + fullPrefix
        );
      }

      String counterStr = latestNumber.substring(fullPrefix.length());

      if (!counterStr.matches("\\d+")) {
        throw new IllegalArgumentException(
          "Invalid last document number: counter part contains non-digit characters."
        );
      }

      number = Integer.parseInt(latestNumber.substring(fullPrefix.length()));
    }

    return fullPrefix + DECIMAL_FORMAT.format(++number);
  }
}
