package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentCategory;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitService;
import de.bund.digitalservice.ris.adm_vwv.application.PublishingFailedException;
import de.bund.digitalservice.ris.adm_vwv.application.converter.business.AdmDocumentationUnitContent;
import de.bund.digitalservice.ris.adm_vwv.config.security.SecurityConfiguration;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LiteratureDocumentationUnitController.class)
@WithMockUser(roles = "literature_user")
@Import(SecurityConfiguration.class)
class LiteratureDocumentationUnitControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DocumentationUnitService documentationUnitService;

  @Test
  @DisplayName("Request GET returns HTTP 200 and data from mocked documentation unit port")
  void find() throws Exception {
    // given
    String documentNumber = "KSLU054920710";
    String json = "{\"test\":\"content\"}";
    given(documentationUnitService.findByDocumentNumber(documentNumber)).willReturn(
      Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), json))
    );

    // when
    mockMvc
      .perform(get("/api/literature/documentation-units/{documentNumber}", documentNumber))
      // then
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.documentNumber").value(documentNumber))
      .andExpect(jsonPath("$.json.test").value("content"));
  }

  @Test
  @DisplayName(
    "Request GET returns HTTP 404 because mocked documentation unit port returns empty optional"
  )
  void find_notFound() throws Exception {
    // given
    String documentNumber = "KSNR000000001";
    given(documentationUnitService.findByDocumentNumber(documentNumber)).willReturn(
      Optional.empty()
    );

    // when
    mockMvc
      .perform(get("/api/literature/documentation-units/{documentNumber}", documentNumber))
      // then
      .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Request POST returns HTTP 201 and data from mocked ULI documentation unit")
  void createUli() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    given(documentationUnitService.create(DocumentCategory.LITERATUR_UNSELBSTSTAENDIG)).willReturn(
      new DocumentationUnit("KSLU054920710", id, null)
    );

    // when
    mockMvc
      .perform(post("/api/literature/uli/documentation-units"))
      // then
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.documentNumber").value("KSLU054920710"));
  }

  @Test
  @DisplayName("Request POST returns HTTP 201 and data from mocked SLI documentation unit")
  void createSli() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    given(documentationUnitService.create(DocumentCategory.LITERATUR_SELBSTSTAENDIG)).willReturn(
      new DocumentationUnit("KSLS054920710", id, null)
    );

    // when
    mockMvc
      .perform(post("/api/literature/sli/documentation-units"))
      // then
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.documentNumber").value("KSLS054920710"));
  }

  @Test
  @DisplayName("Request PUT returns HTTP 200 and data from mocked documentation unit port")
  void update() throws Exception {
    // given
    String documentNumber = "KSLU054920710";
    String json = "{\"test\":\"content\"}";
    given(documentationUnitService.update(documentNumber, json)).willReturn(
      Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), json))
    );

    // when
    mockMvc
      .perform(
        put("/api/literature/documentation-units/{documentNumber}", documentNumber)
          .content(json)
          .contentType(MediaType.APPLICATION_JSON)
      )
      // then
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.documentNumber").value(documentNumber))
      .andExpect(jsonPath("$.json.test").value("content"));
  }

  @Test
  @DisplayName(
    "Request PUT returns HTTP 404 because mocked documentation unit port returns empty optional"
  )
  void update_notFound() throws Exception {
    // given
    String documentNumber = "KSNR000000001";
    String json = "{\"test\":\"unsuccessful\"}";
    given(documentationUnitService.update(documentNumber, json)).willReturn(Optional.empty());

    // when
    mockMvc
      .perform(
        put("/api/literature/documentation-units/{documentNumber}", documentNumber)
          .content(json)
          .contentType(MediaType.APPLICATION_JSON)
      )
      // then
      .andExpect(status().isNotFound());
  }

  @Nested
  @DisplayName("Publish Endpoint")
  class PublishEndpointValidationTests {

    @Test
    @DisplayName("Request PUT on publish returns HTTP 200 for a valid request")
    void publish_success() throws Exception {
      // given
      String documentNumber = "KSLU054920710";
      String validJsonRequest =
        """
        {
          "langueberschrift": "Gültige Überschrift",
          "zitierdaten": ["2023-01-01"],
          "inkrafttretedatum": "2023-01-01",
          "dokumenttyp": { "abbreviation": "TYPE_A", "name": "Type A Document" },
          "normgeberList": [
              {
                "id": "c1d2e3f4-a5b6-7890-1234-567890abcdef",
                "institution": { "name": "Bundesministerium der Justiz", "type": "INSTITUTION" },
                "regions": [{ "code": "DE" }]
              }
          ]
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(AdmDocumentationUnitContent.class))
      ).willReturn(
        Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), validJsonRequest))
      );

      // when
      mockMvc
        .perform(
          put("/api/literature/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documentNumber").value(documentNumber));
    }

    @ParameterizedTest(name = "returns HTTP 400 for invalid field: {0}")
    @MethodSource("invalidPublishPayloads")
    @DisplayName("Request PUT on publish returns HTTP 400 for invalid data")
    void publish_validationFails(String fieldName, String payload) throws Exception {
      // given
      String documentNumber = "KSNR000000001";

      // when
      mockMvc
        .perform(
          put("/api/literature/documentation-units/{documentNumber}/publish", documentNumber)
            .content(payload)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> invalidPublishPayloads() {
      String baseJson =
        """
        {
          "langueberschrift": "Eine gültige Überschrift",
          "zitierdaten": ["2023-10-26"],
          "inkrafttretedatum": "2023-10-26",
          "dokumenttyp": { "abbreviation": "TYPE_A", "name": "Type A Document" },
          "normgeberList": [
            {
              "id": "c1d2e3f4-a5b6-7890-1234-567890abcdef",
              "institution": { "name": "Bundesministerium der Justiz", "type": "INSTITUTION" },
              "regions": [{ "code": "DE" }]
            }
          ]
        }
        """;

      return Stream.of(
        Arguments.of(
          "langueberschrift",
          baseJson.replace(
            "\"langueberschrift\": \"Eine gültige Überschrift\"",
            "\"langueberschrift\": \"  \""
          )
        ),
        Arguments.of(
          "zitierdaten",
          baseJson.replace("\"zitierdaten\": [\"2023-10-26\"]", "\"zitierdaten\": []")
        ),
        Arguments.of(
          "inkrafttretedatum",
          baseJson.replace("\"inkrafttretedatum\": \"2023-10-26\"", "\"inkrafttretedatum\": \"\"")
        ),
        Arguments.of(
          "dokumenttyp",
          baseJson.replace(
            "\"dokumenttyp\": { \"abbreviation\": \"TYPE_A\", \"name\": \"Type A Document\" }",
            "\"dokumenttyp\": null"
          )
        ),
        Arguments.of(
          "normgeberList",
          """
          {
            "langueberschrift": "Eine gültige Überschrift",
            "zitierdaten": ["2023-10-26"],
            "inkrafttretedatum": "2023-10-26",
            "dokumenttyp": { "abbreviation": "TYPE_A", "name": "Type A Document" },
            "normgeberList": []
          }
          """
        )
      );
    }

    @Test
    @DisplayName(
      "Request PUT on publish returns HTTP 404 because mocked documentation unit port returns empty optional"
    )
    void publish_notFound() throws Exception {
      // given
      String documentNumber = "KSNR000000001";
      String validJsonRequest =
        """
        {
          "langueberschrift": "Gültige Überschrift",
          "zitierdaten": ["2023-01-01"],
          "inkrafttretedatum": "2023-01-01",
          "dokumenttyp": { "abbreviation": "TYPE_A", "name": "Type A Document" },
          "normgeberList": [
              {
                "id": "c1d2e3f4-a5b6-7890-1234-567890abcdef",
                "institution": { "name": "Bundesministerium der Justiz", "type": "INSTITUTION" },
                "regions": [{ "code": "DE" }]
              }
          ]
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(AdmDocumentationUnitContent.class))
      ).willReturn(Optional.empty());

      // when
      mockMvc
        .perform(
          put("/api/literature/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Request PUT on publish returns HTTP 503 when external publishing fails")
    void publish_externalFailure() throws Exception {
      // given
      String documentNumber = "KSLU054920710";
      String validJsonRequest =
        """
        {
          "langueberschrift": "Gültige Überschrift",
          "zitierdaten": ["2023-01-01"],
          "inkrafttretedatum": "2023-01-01",
          "dokumenttyp": { "abbreviation": "TYPE_A", "name": "Type A Document" },
          "normgeberList": [
              {
                "id": "c1d2e3f4-a5b6-7890-1234-567890abcdef",
                "institution": { "name": "Bundesministerium der Justiz", "type": "INSTITUTION" },
                "regions": [{ "code": "DE" }]
              }
          ]
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(AdmDocumentationUnitContent.class))
      ).willThrow(
        new PublishingFailedException("External system unavailable", new RuntimeException())
      );

      // when
      mockMvc
        .perform(
          put("/api/literature/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isServiceUnavailable());
    }
  }
}
