package de.bund.digitalservice.ris.adm_literature.documentation_unit.literature.aktivzitierung;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_literature.config.security.SecurityConfiguration;
import de.bund.digitalservice.ris.adm_literature.page.TestPage;
import de.bund.digitalservice.ris.adm_literature.test.WithMockLitUser;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdmAktivzitierungController.class)
@WithMockLitUser
@Import(SecurityConfiguration.class)
class AdmAktivzitierungControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AdmReferenceService admReferenceService;

  @Test
  @DisplayName("GET returns HTTP 200 and adm aktivitierung results in JSON")
  void findAktivzitierungen() throws Exception {
    // given
    UUID id1 = UUID.fromString("7d4f92b1-3e0a-4c2d-9b5a-8f1e6c3d4a2b");
    UUID id2 = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");

    given(admReferenceService.findAktivzitierungen(any())).willReturn(
      TestPage.create(
        List.of(
          new AdmAktivzitierungResult(
            id1,
            "VALID123456789",
            "2023-01-01",
            "The long title",
            "VV",
            List.of("AA"),
            List.of("ABA"),
            List.of("363"),
            List.of()
          ),
          new AdmAktivzitierungResult(
            id2,
            "VALID987654321",
            "2025-01-01",
            "The long title 2",
            "VE",
            List.of("ABI"),
            List.of("AE"),
            List.of("370"),
            List.of()
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
                "documentationUnits": [
                  {
                    "id": "%s",
                    "documentNumber": "VALID123456789",
                    "inkrafttretedatum": "2023-01-01",
                    "langueberschrift": "The long title",
                    "dokumenttyp": "VV",
                    "normgeberList": ["AA"],
                    "fundstellen": ["ABA"],
                    "aktenzeichenList": ["363"],
                    "zitierdaten": []
                  },
                  {
                    "id": "%s",
                    "documentNumber": "VALID987654321",
                    "inkrafttretedatum": "2025-01-01",
                    "langueberschrift": "The long title 2",
                    "dokumenttyp": "VE",
                    "normgeberList": ["ABI"],
                    "fundstellen": ["AE"],
                    "aktenzeichenList": ["370"],
                    "zitierdaten": []
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
