package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentTypeCode;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationOffice;
import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import de.bund.digitalservice.ris.adm_vwv.util.DocumentTypeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class SchemaSelectionInterceptorTest {

  private SchemaSelectionInterceptor interceptor;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private Object handler;

  private MockedStatic<DocumentTypeUtils> mockedDocumentTypeUtils;

  @BeforeEach
  void setUp() {
    interceptor = new SchemaSelectionInterceptor();

    mockedDocumentTypeUtils = Mockito.mockStatic(DocumentTypeUtils.class);

    SchemaContextHolder.clear();
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
    SecurityContextHolder.clearContext();

    mockedDocumentTypeUtils.close();
  }

  @Test
  @DisplayName("preHandle: Defaults to ADM when X-Document-Type header is missing")
  void preHandle_defaultsToADM_whenHeaderIsMissing() {
    // Arrange
    when(request.getHeader("X-Document-Type")).thenReturn(null);
    when(request.getRequestURI()).thenReturn("/api/somepath");

    // Act
    boolean proceed = interceptor.preHandle(request, response, handler);

    // Assert
    assertThat(proceed).isTrue();
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);
  }

  @Test
  @DisplayName("preHandle: Defaults to ADM when X-Document-Type header is invalid")
  void preHandle_defaultsToADM_whenHeaderIsInvalid() {
    // Arrange
    String invalidHeader = "INVALID_VALUE";
    when(request.getHeader("X-Document-Type")).thenReturn(invalidHeader);
    when(request.getRequestURI()).thenReturn("/api/somepath");

    // Act
    boolean proceed = interceptor.preHandle(request, response, handler);

    // Assert
    assertThat(proceed).isTrue();
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);
  }

  @Test
  @DisplayName("preHandle: Sets ADM schema for VERWALTUNGSVORSCHRIFTEN")
  void preHandle_setsADM_forVerwaltungsvorschriften() {
    // Arrange
    when(request.getHeader("X-Document-Type")).thenReturn(
      DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN.name()
    );
    when(request.getRequestURI()).thenReturn("/api/somepath");

    // Act
    boolean proceed = interceptor.preHandle(request, response, handler);

    // Assert
    assertThat(proceed).isTrue();
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);
  }

  @Test
  @DisplayName("preHandle: Sets LIT schema for LITERATUR_SELBSTSTAENDIG")
  void preHandle_setsLIT_forSelbststaendig() {
    // Arrange
    when(request.getHeader("X-Document-Type")).thenReturn(
      DocumentTypeCode.LITERATUR_SELBSTSTAENDIG.name()
    );
    when(request.getRequestURI()).thenReturn("/api/somepath");

    // Act
    boolean proceed = interceptor.preHandle(request, response, handler);

    // Assert
    assertThat(proceed).isTrue();
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LIT);
  }

  @Test
  @DisplayName("preHandle: Sets LIT schema for LITERATUR_UNSELBSTSTAENDIG")
  void preHandle_setsLIT_forUnselbststaendig() {
    // Arrange
    when(request.getHeader("X-Document-Type")).thenReturn(
      DocumentTypeCode.LITERATUR_UNSELBSTSTAENDIG.name()
    );
    when(request.getRequestURI()).thenReturn("/api/somepath");

    // Act
    boolean proceed = interceptor.preHandle(request, response, handler);

    // Assert
    assertThat(proceed).isTrue();
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LIT);
  }

  @Test
  @DisplayName("preHandle: Skips schema logic for /environment path")
  void preHandle_skipsLogicForEnvironmentPath() {
    // Arrange
    when(request.getRequestURI()).thenReturn("/environment");

    // Act
    boolean proceed = interceptor.preHandle(request, response, handler);

    // Assert
    assertThat(proceed).isTrue();
    // Schema should not be set
    assertThat(SchemaContextHolder.getSchema()).isNull();
    // Verify no other schema logic logs were made
    verifyNoInteractions(response);
  }

  // --- afterCompletion Tests ---

  @Test
  @DisplayName("afterCompletion: Clears ThreadLocal")
  void afterCompletion_clearsThreadLocalAndLogsWithUserDetails() {
    // Arrange
    SchemaContextHolder.setSchema(SchemaType.ADM);
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/api/docs");
    when(response.getStatus()).thenReturn(200);

    // Mock DocumentType
    mockedDocumentTypeUtils
      .when(DocumentTypeUtils::getDocumentTypeCode)
      .thenReturn(DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN);

    // Mock SecurityContext
    UserDocumentDetails userDetails = new UserDocumentDetails(
      DocumentationOffice.BAG,
      DocumentTypeCode.VERWALTUNGSVORSCHRIFTEN
    );
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userDetails);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Act
    interceptor.afterCompletion(request, response, handler, null);

    // Assert
    assertThat(SchemaContextHolder.getSchema()).isNull();
  }
}
