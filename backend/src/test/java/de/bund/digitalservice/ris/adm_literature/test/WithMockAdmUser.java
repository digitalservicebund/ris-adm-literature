package de.bund.digitalservice.ris.adm_literature.test;

import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationOffice;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mock adm user (BSG and ADM)
 */
@Retention(RetentionPolicy.RUNTIME)
@WithMockDocumentUser(
  office = DocumentationOffice.BSG,
  category = DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
  roles = "ROLE_adm_user"
)
public @interface WithMockAdmUser {
}
