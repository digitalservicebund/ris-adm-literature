package de.bund.digitalservice.ris.adm_literature.test;

import de.bund.digitalservice.ris.adm_literature.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.application.DocumentationOffice;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mock adm user (BSG and ADM)
 */
@Retention(RetentionPolicy.RUNTIME)
@WithMockDocumentUser(
  office = DocumentationOffice.BSG,
  category = DocumentCategory.VERWALTUNGSVORSCHRIFTEN
)
public @interface WithMockAdmUser {
}
