package de.bund.digitalservice.ris.adm_vwv.test;

import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockUserSecurityContextFactory
  implements WithSecurityContextFactory<WithMockDocumentUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockDocumentUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    var principal = new UserDocumentDetails(annotation.office(), annotation.category());

    var auth = new UsernamePasswordAuthenticationToken(principal, "password");

    context.setAuthentication(auth);
    return context;
  }
}
