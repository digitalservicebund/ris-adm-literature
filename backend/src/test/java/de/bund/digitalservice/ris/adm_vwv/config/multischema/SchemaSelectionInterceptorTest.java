package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentTypeCode;
import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class SchemaSelectionInterceptorTest {

  private final SchemaSelectionInterceptor interceptor = new SchemaSelectionInterceptor();
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  void setUp() {
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    SchemaContextHolder.clear();
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
    SecurityContextHolder.clearContext();
  }

  @Test
  void preHandle_defaultsToADM_whenNoAuthentication() {
    SecurityContextHolder.clearContext();

    boolean proceed = interceptor.preHandle(request, response, new Object());

    assertThat(proceed).isTrue();
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);
  }

  @Test
  void preHandle_setsADM_forVerwaltungsvorschriften() {
    mockAuthWithType(DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN);

    interceptor.preHandle(request, response, new Object());

    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);
  }

  @Test
  void preHandle_setsLIT_forSelbststaendig() {
    mockAuthWithType(DocumentTypeCode.LITERATUR_SELBSTSTAENDIG);

    interceptor.preHandle(request, response, new Object());

    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LIT);
  }

  @Test
  void preHandle_setsLIT_forUnselbststaendig() {
    mockAuthWithType(DocumentTypeCode.LITERATUR_UNSELBSTSTAENDIG);

    interceptor.preHandle(request, response, new Object());

    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LIT);
  }

  @Test
  void afterCompletion_clearsThreadLocal() {
    SchemaContextHolder.setSchema(SchemaType.LIT);

    interceptor.afterCompletion(request, response, new Object(), null);

    assertThat(SchemaContextHolder.getSchema()).isNull();
  }

  private void mockAuthWithType(DocumentTypeCode type) {
    UserDocumentDetails details = new UserDocumentDetails(null, type);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(details);

    SecurityContext context = Mockito.mock(SecurityContext.class);
    when(context.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(context);
  }
}
