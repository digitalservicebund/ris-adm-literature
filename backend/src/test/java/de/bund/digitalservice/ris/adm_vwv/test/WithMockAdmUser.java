package de.bund.digitalservice.ris.adm_vwv.test;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationOffice;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Mock adm user (BSG and ADM)
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAdmUserSecurityContextFactory.class)
public @interface WithMockAdmUser {
  /**
   * The documentation office for the mock user.
   */
  DocumentationOffice office() default DocumentationOffice.BSG;

  /**
   * The document category for the mock user.
   */
  DocumentCategory category() default DocumentCategory.VERWALTUNGSVORSCHRIFTEN;
}
