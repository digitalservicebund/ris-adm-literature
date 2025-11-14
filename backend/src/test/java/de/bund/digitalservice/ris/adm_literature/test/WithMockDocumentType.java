package de.bund.digitalservice.ris.adm_literature.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * A custom annotation that sets up a MockHttpServletRequest in the
 * RequestContextHolder with the 'X-Document-Type' header.
 * <p>
 * This is used to satisfy service-layer dependencies that read this header
 * during integration tests.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ExtendWith(WithMockDocumentTypeExtension.class)
public @interface WithMockDocumentType {
  /**
   * The value to be set for the 'X-Document-Type' header.
   * Defaults to "VERWALTUNGSVORSCHRIFTEN".
   */
  String value() default "VERWALTUNGSVORSCHRIFTEN";
}
