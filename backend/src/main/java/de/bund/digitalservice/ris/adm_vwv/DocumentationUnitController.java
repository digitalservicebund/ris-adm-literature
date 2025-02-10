package de.bund.digitalservice.ris.adm_vwv;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DocumentationUnitController {

    private final CreateDocumentationUnitPort createDocumentationUnitPort;

    /**
     * Creates a new documentation unit with a new document number in database and returns it.
     *
     * @return Created documentation unit
     */
    @PostMapping("api/documentation-units")
    public DocumentationUnit create() {
        return createDocumentationUnitPort.create();
    }

}
