package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import java.lang.annotation.*;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockJwtSecurityContextFactory.class)
public @interface WithMockJwt {
  long value() default 1L;

  String[] roles() default { "ROLE_adm_vwv_user" };
}
