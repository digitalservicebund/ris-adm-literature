package de.bund.digitalservice.ris.adm_literature.lookup_tables.verweistyp;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

/**
 * Citation type JPA entity ('Verweistyp').
 * <p>
 *   This entity maps a database view. The view itself uses database schema {@code lookup_tables}
 *   (but same database) which is not owned by {@code ris-adm-literature}.
 * </p>
 *
 * @see VerweisTypRepository
 */
@Entity
@Data
@Table(name = "verweis_typ_view")
@Immutable
public class VerweisTypEntity {

  @Id
  @GeneratedValue
  private UUID id;

  @Column
  private String name;

  @Column
  private String typNummer;

  @Column
  private String publicId;
}
