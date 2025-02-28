package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.Year;
import java.util.UUID;

@Entity
@Data
@Table(name = "test_table")
public class TestEntity {

  @Id
  @GeneratedValue
  private UUID id;

  /**
   * The latest document number used. The complete string is persisted here, e.g. {@code KSNR2025000001}.
   */
  @Basic(optional = false)
  private String latest;

  @Basic(optional = false)
  private Year year;

  public Integer getLatestNumber() {
    return Integer.parseInt(StringUtils.substringAfter(latest, "KSNR"));
  }
}
