package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

/**
 * Document type entity.
 */
@Entity
@Data
@Table(name = "document_types_view")
public class DocumentTypeEntity {

  @Id
  private UUID id;

  private String abbreviation;

  private String name;
}
