package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationOffice;
import de.bund.digitalservice.ris.adm_vwv.config.security.UserDocumentDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class TenantContextFilterTest {

  @Mock
  private FilterChain mockFilterChain;

  private MockHttpServletRequest mockRequest;
  private MockHttpServletResponse mockResponse;
  private TenantContextFilter tenantContextFilter;

  @BeforeEach
  void setUp() {
    mockRequest = new MockHttpServletRequest();
    mockResponse = new MockHttpServletResponse();
    tenantContextFilter = new TenantContextFilter();

    SecurityContextHolder.clearContext();
    SchemaContextHolder.clear();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
    SchemaContextHolder.clear();
  }

  /** Helper to set up a base authenticated user in the SecurityContext. */
  private void setupAuthentication(UserDocumentDetails principal) {
    Authentication auth = new UsernamePasswordAuthenticationToken(
      principal,
      null,
      Collections.emptyList()
    );
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);
  }

  @Test
  @DisplayName("Should set ADM schema and update principal for VERWALTUNGSVORSCHRIFTEN header")
  void doFilterInternal_withValidAdmHeader_setsSchemaAndUpdatesPrincipal()
    throws ServletException, IOException {
    UserDocumentDetails basePrincipal = new UserDocumentDetails(DocumentationOffice.BSG, null);
    setupAuthentication(basePrincipal);
    mockRequest.setRequestURI("/api/some/endpoint");
    mockRequest.addHeader("X-Document-Type", "VERWALTUNGSVORSCHRIFTEN");

    doAnswer(_ -> {
      assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      UserDocumentDetails principal = (UserDocumentDetails) auth.getPrincipal();
      assertThat(principal.office()).isEqualTo(DocumentationOffice.BSG);
      assertThat(principal.documentCategory()).isEqualTo(DocumentCategory.VERWALTUNGSVORSCHRIFTEN);

      return null;
    })
      .when(mockFilterChain)
      .doFilter(mockRequest, mockResponse);

    tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    assertThat(SchemaContextHolder.getSchema()).isNull();
  }

  @Test
  @DisplayName("Should set LIT schema and update principal for LITERATUR_SELBSTSTAENDIG header")
  void doFilterInternal_withValidLitHeader_setsSchemaAndUpdatesPrincipal()
    throws ServletException, IOException {
    UserDocumentDetails basePrincipal = new UserDocumentDetails(DocumentationOffice.BAG, null);
    setupAuthentication(basePrincipal);
    mockRequest.setRequestURI("/api/lit/endpoint");
    mockRequest.addHeader("X-Document-Type", "LITERATUR_SELBSTSTAENDIG");

    doAnswer(_ -> {
      assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LIT);

      UserDocumentDetails principal = (UserDocumentDetails) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
      assertThat(principal.office()).isEqualTo(DocumentationOffice.BAG);
      assertThat(principal.documentCategory()).isEqualTo(DocumentCategory.LITERATUR_SELBSTSTAENDIG);

      return null;
    })
      .when(mockFilterChain)
      .doFilter(mockRequest, mockResponse);

    tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    assertThat(SchemaContextHolder.getSchema()).isNull();
  }

  @Test
  @DisplayName("Should skip all logic for /actuator paths")
  void doFilterInternal_forActuatorPath_skipsAllLogic() throws ServletException, IOException {
    mockRequest.setRequestURI("/actuator/health");

    tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);

    assertThat(SchemaContextHolder.getSchema()).isNull();

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @DisplayName("Should skip all logic for /environment path")
  void doFilterInternal_forEnvironmentPath_skipsAllLogic() throws ServletException, IOException {
    mockRequest.setRequestURI("/environment");

    tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    assertThat(SchemaContextHolder.getSchema()).isNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  @DisplayName("Should default to ADM and not update principal when header is missing")
  void doFilterInternal_withMissingHeader_defaultsToAdmAndNotUpdatePrincipal()
    throws ServletException, IOException {
    UserDocumentDetails basePrincipal = new UserDocumentDetails(DocumentationOffice.BSG, null);
    setupAuthentication(basePrincipal);
    mockRequest.setRequestURI("/api/test");

    doAnswer(_ -> {
      assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);

      UserDocumentDetails principal = (UserDocumentDetails) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
      assertThat(principal.documentCategory()).isNull();

      return null;
    })
      .when(mockFilterChain)
      .doFilter(mockRequest, mockResponse);

    tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    assertThat(SchemaContextHolder.getSchema()).isNull();
  }

  @Test
  @DisplayName("Should default to ADM and not update principal when header is invalid")
  void doFilterInternal_withInvalidHeader_defaultsToAdmAndNotUpdatePrincipal()
    throws ServletException, IOException {
    UserDocumentDetails basePrincipal = new UserDocumentDetails(DocumentationOffice.BSG, null);
    setupAuthentication(basePrincipal);
    mockRequest.setRequestURI("/api/test");
    mockRequest.addHeader("X-Document-Type", "INVALID_VALUE");

    doAnswer(_ -> {
      assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);

      UserDocumentDetails principal = (UserDocumentDetails) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
      assertThat(principal.documentCategory()).isNull();

      return null;
    })
      .when(mockFilterChain)
      .doFilter(mockRequest, mockResponse);

    tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    assertThat(SchemaContextHolder.getSchema()).isNull();
  }

  @Test
  @DisplayName("Should set schema but skip principal update for unauthenticated requests")
  void doFilterInternal_unauthenticatedRequest_setsSchemaOnly()
    throws ServletException, IOException {
    mockRequest.setRequestURI("/api/some/endpoint");
    mockRequest.addHeader("X-Document-Type", "VERWALTUNGSVORSCHRIFTEN");

    doAnswer(_ -> {
      assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);

      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

      return null;
    })
      .when(mockFilterChain)
      .doFilter(mockRequest, mockResponse);

    tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    assertThat(SchemaContextHolder.getSchema()).isNull();
  }

  @Test
  @DisplayName("Should clear SchemaContextHolder even if filter chain throws exception")
  void doFilterInternal_whenChainThrowsException_clearsSchemaContext()
    throws ServletException, IOException {
    setupAuthentication(new UserDocumentDetails(DocumentationOffice.BAG, null));
    mockRequest.setRequestURI("/api/test");
    mockRequest.addHeader("X-Document-Type", "VERWALTUNGSVORSCHRIFTEN");

    ServletException testException = new ServletException("Chain failed");
    doThrow(testException).when(mockFilterChain).doFilter(mockRequest, mockResponse);

    assertThatExceptionOfType(ServletException.class)
      .isThrownBy(() ->
        tenantContextFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain)
      )
      .isEqualTo(testException);

    assertThat(SchemaContextHolder.getSchema()).isNull();
  }
}
