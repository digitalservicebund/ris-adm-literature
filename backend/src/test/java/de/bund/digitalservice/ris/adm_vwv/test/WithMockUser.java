package de.bund.digitalservice.ris.adm_vwv.test;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationOffice;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Generic mock user.
 * Configures the SecurityContext with a UserDocumentDetails principal.
 * This is the base annotation; prefer using @WithMockAdmUser or @WithMockLitUser in tests.
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory.class)
public @interface WithMockUser {
  /**
   * The documentation office for the mock user.
   */
  DocumentationOffice office() default DocumentationOffice.BSG;

  /**
   * The document category of document the user works with.
   */
  DocumentCategory category() default DocumentCategory.VERWALTUNGSVORSCHRIFTEN;
}
