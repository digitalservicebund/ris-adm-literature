package de.bund.digitalservice.ris.adm_vwv.test;

import java.util.Optional;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WithMockDocumentTypeExtension implements BeforeEachCallback, AfterEachCallback {

  private static final String HEADER_NAME = "X-Document-Type";
  private static final String DEFAULT_DOC_TYPE = "VERWALTUNGSVORSCHRIFTEN";

  @Override
  public void beforeEach(ExtensionContext context) {
    String docType = getDocType(context);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HEADER_NAME, docType);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Override
  public void afterEach(ExtensionContext context) {
    RequestContextHolder.resetRequestAttributes();
  }

  /**
   * Finds the @WithMockDocumentType annotation on the test method or class
   * and returns the specified header value.
   */
  private String getDocType(ExtensionContext context) {
    Optional<WithMockDocumentType> onMethod = Optional.ofNullable(
      AnnotationUtils.findAnnotation(context.getRequiredTestMethod(), WithMockDocumentType.class)
    );

    if (onMethod.isPresent()) {
      return onMethod.get().value();
    }

    return Optional.ofNullable(
      AnnotationUtils.findAnnotation(context.getRequiredTestClass(), WithMockDocumentType.class)
    )
      .map(WithMockDocumentType::value)
      .orElse(DEFAULT_DOC_TYPE);
  }
}
