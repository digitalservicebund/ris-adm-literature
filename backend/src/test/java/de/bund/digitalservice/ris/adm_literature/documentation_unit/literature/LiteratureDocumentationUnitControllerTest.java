package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_literature.config.security.SecurityConfiguration;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.AdmAktivzitierungOverviewElement;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnit;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.DocumentationUnitService;
import de.bund.digitalservice.ris.adm_literature.documentation_unit.publishing.PublishingFailedException;
import de.bund.digitalservice.ris.adm_literature.page.TestPage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

  @Nested
  @DisplayName("GET multiple documents")
  class GetMultipleDocuments {

    @Test
    @DisplayName("GET returns HTTP 200 and sliReferenceSearchOverview in JSON")
    void getDocsFormatted() throws Exception {
      // given
      given(
        documentationUnitService.findLiteratureDocumentationUnitOverviewElements(any())
      ).willReturn(
        TestPage.create(
          List.of(
            new LiteratureDocumentationUnitOverviewElement(
              UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
              "VALID123456789",
              "1999-2022",
              "Dies ist der Hauptsachtitel",
              List.of("DA"),
              List.of("Name 1", "Name 2")
            ),
            new LiteratureDocumentationUnitOverviewElement(
              UUID.fromString("33385f64-5717-4562-b3fc-2c963f66afa6"),
              "VALID987654321",
              "2025",
              "Dies ist der 2. Hauptsachtitel",
              List.of("DV"),
              List.of("Name 3", "Name 4")
            )
          )
        )
      );

      // when & then
      mockMvc
        .perform(get("/api/literature/sli/documentation-units"))
        .andExpect(status().isOk())
        .andExpect(
          content()
            .json(
              """

                {
                "documentationUnitsOverview": [
                  {
                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                    "documentNumber": "VALID123456789",
                    "veroeffentlichungsjahr": "1999-2022",
                    "titel": "Dies ist der Hauptsachtitel",
                    "dokumenttypen": [
                      "DA"
                    ],
                    "verfasser": [
                      "Name 1",
                      "Name 2"
                    ]
                  },
                  {
                    "id": "33385f64-5717-4562-b3fc-2c963f66afa6",
                    "documentNumber": "VALID987654321",
                    "veroeffentlichungsjahr": "2025",
                    "titel": "Dies ist der 2. Hauptsachtitel",
                    "dokumenttypen": [
                      "DV"
                    ],
                    "verfasser": [
                      "Name 3",
                      "Name 4"
                    ]
                  }
                ],
                "page": {
                  "size": 2,
                  "number": 0,
                  "numberOfElements": 2,
                  "totalElements": 2,
                  "first": true,
                  "last": true,
                  "empty": false
                }
              }
              """
            )
        );
    }
  }

  @Nested
  @DisplayName("GET single document")
  class GetSingleDocument {

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
  }

  @Nested
  @DisplayName("POST to create document")
  class PostToDocument {

    @Test
    @DisplayName("Request POST returns HTTP 201 and data from mocked ULI documentation unit")
    void createUli() throws Exception {
      // given
      UUID id = UUID.randomUUID();
      given(documentationUnitService.create(DocumentCategory.LITERATUR_UNSELBSTAENDIG)).willReturn(
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
      given(documentationUnitService.create(DocumentCategory.LITERATUR_SELBSTAENDIG)).willReturn(
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
  }

  @Nested
  @DisplayName("PUT a single document")
  class PutSingleDocument {

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
  }

  @Nested
  @DisplayName("ULI Publish Endpoint")
  class UliPublishEndpointTests {

    @DisplayName(
      "Request PUT on publish returns HTTP 200 for valid ULI request (supporting both paths)"
    )
    @Test
    void publish_uli_success() throws Exception {
      // given
      String documentNumber = "KSLU054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLU054920710",
          "veroeffentlichungsjahr": "2025",
          "hauptsachtitel": "titel"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(UliDocumentationUnitContent.class))
      ).willReturn(
        Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), validJsonRequest))
      );

      // when
      mockMvc
        .perform(
          put("/api/literature/uli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documentNumber").value(documentNumber));
    }

    @Test
    @DisplayName(
      "Request PUT on ULI publish returns HTTP 404 because mocked documentation unit port returns empty optional"
    )
    void publish_uli_notFound() throws Exception {
      // given
      String documentNumber = "KSLU054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLU054920710",
          "veroeffentlichungsjahr": "2025",
          "hauptsachtitel": "titel"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(UliDocumentationUnitContent.class))
      ).willReturn(Optional.empty());

      // when
      mockMvc
        .perform(
          put("/api/literature/uli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Request PUT on ULI publish returns HTTP 503 when external publishing fails")
    void publish_uli_externalFailure() throws Exception {
      // given
      String documentNumber = "KSLU054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLU054920710",
          "veroeffentlichungsjahr": "2025",
          "hauptsachtitel": "titel"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(UliDocumentationUnitContent.class))
      ).willThrow(
        new PublishingFailedException("External system unavailable", new RuntimeException())
      );

      // when
      mockMvc
        .perform(
          put("/api/literature/uli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("Request PUT on ULI publish returns HTTP 400 when data is missing")
    void publish_uli_validationFailure() throws Exception {
      // given
      String documentNumber = "KSLU054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLU054920710",
          "veroeffentlichungsjahr": "2025"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(UliDocumentationUnitContent.class))
      ).willReturn(Optional.empty());

      // when
      mockMvc
        .perform(
          put("/api/literature/uli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("SLI Publish Endpoint")
  class SliPublishEndpointTests {

    @Test
    @DisplayName("Request PUT on SLI publish returns HTTP 200 for a valid request")
    void publish_sli_success() throws Exception {
      // given
      String documentNumber = "KSLS054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLS054920710",
          "veroeffentlichungsjahr": "2025",
          "hauptsachtitel": "titel"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(SliDocumentationUnitContent.class))
      ).willReturn(
        Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), validJsonRequest))
      );

      // when
      mockMvc
        .perform(
          put("/api/literature/sli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documentNumber").value(documentNumber));
    }

    @Test
    @DisplayName(
      "Request PUT on SLI publish returns HTTP 404 because mocked documentation unit port returns empty optional"
    )
    void publish_sli_notFound() throws Exception {
      // given
      String documentNumber = "KSLS054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLS054920710",
          "veroeffentlichungsjahr": "2025",
          "hauptsachtitel": "titel"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(SliDocumentationUnitContent.class))
      ).willReturn(Optional.empty());

      // when
      mockMvc
        .perform(
          put("/api/literature/sli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Request PUT on SLI publish returns HTTP 503 when external publishing fails")
    void publish_sli_externalFailure() throws Exception {
      // given
      String documentNumber = "KSLS054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLS054920710",
          "veroeffentlichungsjahr": "2025",
          "hauptsachtitel": "titel"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(SliDocumentationUnitContent.class))
      ).willThrow(
        new PublishingFailedException("External system unavailable", new RuntimeException())
      );

      // when
      mockMvc
        .perform(
          put("/api/literature/sli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isServiceUnavailable());
    }

    @Test
    @DisplayName("Request PUT on SLI publish returns HTTP 400 when data is missing")
    void publish_uli_validationFailure() throws Exception {
      // given
      String documentNumber = "KSLU054920710";
      String validJsonRequest =
        """
        {
          "documentNumber": "KSLU054920710",
          "veroeffentlichungsjahr": "2025"
        }""";

      given(
        documentationUnitService.publish(any(String.class), any(SliDocumentationUnitContent.class))
      ).willReturn(Optional.empty());

      // when
      mockMvc
        .perform(
          put("/api/literature/sli/documentation-units/{documentNumber}/publish", documentNumber)
            .content(validJsonRequest)
            .contentType(MediaType.APPLICATION_JSON)
        )
        // then
        .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET Aktivzitierungen (ADM)")
  class GetAktivzitierungenAdm {

    @Test
    @DisplayName("GET returns HTTP 200 and admAktivzitierungenOverview in JSON")
    void getAktivzitierungenFormatted() throws Exception {
      // given
      UUID id1 = UUID.fromString("7d4f92b1-3e0a-4c2d-9b5a-8f1e6c3d4a2b");
      UUID id2 = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");

      given(documentationUnitService.findAktivzitierungen(any())).willReturn(
        TestPage.create(
          List.of(
            new AdmAktivzitierungOverviewElement(
              id1,
              "VALID123456789",
              "2023-01-01",
              "The long title",
              "VV",
              List.of("AA"),
              List.of("ABA"),
              List.of("363")
            ),
            new AdmAktivzitierungOverviewElement(
              id2,
              "VALID987654321",
              "2025-01-01",
              "The long title 2",
              "VE",
              List.of("ABI"),
              List.of("AE"),
              List.of("370")
            )
          )
        )
      );

      // when & then
      mockMvc
        .perform(
          get("/api/literature/aktivzitierungen/adm")
            .param("documentNumber", "VALID")
            .param("periodikum", "ABA")
        )
        .andExpect(status().isOk())
        .andExpect(
          content()
            .json(
              String.format(
                """
                {
                  "documentationUnitsOverview": [
                    {
                      "id": "%s",
                      "documentNumber": "VALID123456789",
                      "inkrafttretedatum": "2023-01-01",
                      "langueberschrift": "The long title",
                      "dokumenttyp": "VV",
                      "normgeber": ["AA"],
                      "periodikum": ["ABA"],
                      "zitatstelle": ["§4"],
                      "aktenzeichen": ["363"]
                    },
                    {
                      "id": "%s",
                      "documentNumber": "VALID987654321",
                      "inkrafttretedatum": "2025-01-01",
                      "langueberschrift": "The long title 2",
                      "dokumenttyp": "VE",
                      "normgeber": ["ABI"],
                      "periodikum": ["AE"],
                      "zitatstelle": ["§6"],
                      "aktenzeichen": ["370"]
                    }
                  ],
                  "page": {
                    "size": 2,
                    "number": 0,
                    "numberOfElements": 2,
                    "totalElements": 2,
                    "first": true,
                    "last": true,
                    "empty": false
                  }
                }
                """,
                id1,
                id2
              )
            )
        );
    }
  }
}
