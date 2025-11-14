package de.bund.digitalservice.ris.adm_literature.test;

import de.bund.digitalservice.ris.adm_literature.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.application.DocumentationOffice;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mock lit user (BAG and ULI)
 */
@Retention(RetentionPolicy.RUNTIME)
@WithMockDocumentUser(
  office = DocumentationOffice.BAG,
  category = DocumentCategory.LITERATUR_UNSELBSTSTAENDIG
)
public @interface WithMockLitUser {
}
