package de.bund.digitalservice.ris.adm_literature.jpa;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Embeddable JPA class for auditing metadata.
 */
@Embeddable
@Data
public class AuditingMetadata {

  @CreatedDate
  private LocalDateTime publishedAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
}
