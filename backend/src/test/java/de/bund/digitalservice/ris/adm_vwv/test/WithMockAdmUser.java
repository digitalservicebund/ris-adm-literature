package de.bund.digitalservice.ris.adm_vwv.test;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationOffice;
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
