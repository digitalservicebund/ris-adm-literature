package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentTypeCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    when(request.getHeader("X-Document-Type")).thenReturn(
      DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN.name()
    );

    interceptor.preHandle(request, response, new Object());

    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);
  }

  @Test
  void preHandle_setsLIT_forSelbststaendig() {
    when(request.getHeader("X-Document-Type")).thenReturn(
      DocumentTypeCode.LITERATUR_SELBSTSTAENDIG.name()
    );

    interceptor.preHandle(request, response, new Object());

    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LIT);
  }

  @Test
  void preHandle_setsLIT_forUnselbststaendig() {
    when(request.getHeader("X-Document-Type")).thenReturn(
      DocumentTypeCode.LITERATUR_UNSELBSTSTAENDIG.name()
    );

    interceptor.preHandle(request, response, new Object());

    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LIT);
  }

  @Test
  void afterCompletion_clearsThreadLocal() {
    SchemaContextHolder.setSchema(SchemaType.LIT);

    interceptor.afterCompletion(request, response, new Object(), null);

    assertThat(SchemaContextHolder.getSchema()).isNull();
  }
}
