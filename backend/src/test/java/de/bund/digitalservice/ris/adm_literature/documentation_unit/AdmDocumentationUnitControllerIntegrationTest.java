package de.bund.digitalservice.ris.adm_literature.documentation_unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.adm_literature.config.security.SecurityConfiguration;
import de.bund.digitalservice.ris.adm_literature.test.WithMockAdmUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

// MOCK environment for MockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@WithMockAdmUser
@Import(SecurityConfiguration.class)
class AdmDocumentationUnitControllerIntegrationTest {

  // Holds all the running applicationcomponents
  @Autowired
  private WebApplicationContext webApplicationContext;

  // Used to perform the request (initialized manually)
  private MockMvc mockMvc;

  // Real service; for confirming that the full application context was loaded
  @Autowired
  private DocumentationUnitService documentationUnitService;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Setup MockMvc before each test to use the entire application context.
   * This allows MockMvc to process requests through the real filter chain (e.g.,
   * Security).
   */
  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }

  @Disabled
  @Test
  void verifyContextLoads() {
    // Assert that the real service bean was successfully injected.
    assertThat(documentationUnitService).isNotNull();
  }

  @Test
  void documentSholdPublishFine() throws Exception {
    // given
    MvcResult mvcResult = mockMvc
      .perform(post("/api/adm/documentation-units"))
      .andDo(print())
      .andExpect(status().isCreated())
      .andReturn();

    String responseString = mvcResult.getResponse().getContentAsString();

    DocumentationUnit documentationUnit = objectMapper.readValue(
      responseString,
      DocumentationUnit.class
    );

    String documentNumber = documentationUnit.documentNumber();

    // when
    mockMvc
      .perform(
        put("/api/adm/documentation-units/" + documentNumber + "/publish")
          .content(DOCUMENT_UNIT_JSON_TEXT_BLOCK)
          .contentType(MediaType.APPLICATION_JSON)
      )
      // then
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.json.documentNumber").value(documentNumber));
  }

  String DOCUMENT_UNIT_JSON_TEXT_BLOCK =
    """
    {
        "id": "ba3cb804-814d-452e-ae59-2912bc41d3ce",
        "documentNumber": "KSNR2025000018",
        "fieldsOfLaw": [],
        "activeCitations": [
            {
                "uuid": "25aef667-3417-4fda-974c-b2e5c4d6e2bf",
                "newEntry": true,
                "court": {
                    "id": "00e1b035-a7f4-4d88-b5c0-a7d0466b8752",
                    "type": "AG",
                    "location": "Aachen"
                },
                "decisionDate": "2011-01-01",
                "fileNumber": "az",
                "citationType": {
                    "uuid": "e52c14ac-1a5b-4ed2-9228-516489dd9f2a",
                    "jurisShortcut": "Abgrenzung",
                    "label": "Abgrenzung"
                }
            }
        ],
        "normReferences": [
            {
                "normAbbreviation": {
                    "id": "03f7c912-a2d1-4b3e-9d2a-41c2a8e5c1f7",
                    "abbreviation": "SGB 5",
                    "officialLongTitle": "Sozialgesetzbuch (SGB) Fünftes Buch (V)"
                },
                "singleNorms": []
            }
        ],
        "note": "",
        "fundstellen": [
            {
                "id": "3805c535-3e14-4936-9636-5697b9e24d3a",
                "periodikum": {
                    "id": "881e6def-61a4-4a30-8ca7-cb1c66f8d240",
                    "abbreviation": "AA",
                    "publicId": "aa",
                    "title": "Arbeitsrecht aktiv",
                    "subtitle": "Arbeitsrecht optimal gestalten und erfolgreich anwenden",
                    "citationStyle": "2009, 55-59; AA &, 2015, 6-13 (Sonderausgabe)"
                },
                "zitatstelle": "ddrh"
            }
        ],
        "langueberschrift": "nrdnrd",
        "zitierdaten": [
            "2011-01-01"
        ],
        "dokumenttyp": {
            "abbreviation": "GB",
            "name": "allgemeine Geschäftsbedingungen"
        },
        "inkrafttretedatum": "2011-01-01",
        "ausserkrafttretedatum": "2011-01-01",
        "normgeberList": [
            {
                "id": "eb14692f-0bda-4b2c-adb1-72e0072b8d36",
                "institution": {
                    "id": "f0a74bfd-8eb8-4cd2-afed-715b226db741",
                    "name": "AA",
                    "officialName": "Auswärtiges Amt",
                    "type": "INSTITUTION",
                    "regions": []
                },
                "regions": [
                    {
                        "id": "236a9e21-c6c2-4f40-aa70-c2ec6dc3c65e",
                        "code": "AG",
                        "longText": "Altes Gebiet"
                    }
                ]
            }
        ]
    }
    """;
}
