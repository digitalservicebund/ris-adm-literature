package de.bund.digitalservice.ris.adm_vwv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentationUnitService implements CreateDocumentationUnitPort {

    private final DocumentationUnitPersistencePort documentationUnitPersistencePort;

    @Override
    public DocumentationUnit create() {
        return documentationUnitPersistencePort.create();
    }
}
