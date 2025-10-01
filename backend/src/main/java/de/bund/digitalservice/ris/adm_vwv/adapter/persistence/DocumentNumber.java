package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import java.text.DecimalFormat;
import java.time.Year;
import java.util.Objects;

public class DocumentNumber {

  private static final String LEGACY_PREFIX = "KSNR";
  private static final int COUNTER_LENGTH = 6;
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0".repeat(COUNTER_LENGTH));

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

  /**
   * The LEGACY constructor for the original KSNR document numbers.
   * It calls the main constructor with the hardcoded "KSNR" prefix.
   * @param year The current year.
   * @param latestNumber The last persisted number for the KSNR series, or null.
   */
  public DocumentNumber(Year year, String latestNumber) {
    this(LEGACY_PREFIX, year, latestNumber);
  }

  public String create() {
    String fullPrefix = this.prefix + this.year.getValue();
    int number = 0;

    if (latestNumber != null) {
      // Check that the latest number starts with the expected prefix
      if (!latestNumber.startsWith(fullPrefix)) {
        throw new IllegalArgumentException(
          "Invalid last document number: " + latestNumber + " does not match prefix " + fullPrefix
        );
      }
      // The counter is the part of the string after the full prefix
      number = Integer.parseInt(latestNumber.substring(fullPrefix.length()));
    }

    return fullPrefix + DECIMAL_FORMAT.format(++number);
  }
}
