package de.bund.digitalservice.ris.adm_vwv.adapter.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "documentation_unit")
public class DocumentationUnitEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String documentNumber;

    private String json;
}
