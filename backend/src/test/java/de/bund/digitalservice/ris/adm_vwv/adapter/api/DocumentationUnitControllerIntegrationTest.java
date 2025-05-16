package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitPort;
import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DocumentationUnitController.class)
@Import(SecurityConfiguration.class)
class DocumentationUnitControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DocumentationUnitPort documentationUnitPort;

  @Nested
  class GetListOfDocumentUnits {

    @Test
    @DisplayName("returns HTTP 200")
    void getListOfDocumentsSuccess() throws Exception {
      // given

      // when
      mockMvc
        .perform(get("/api/documentation-units"))
        // then
        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("returns list of (uu)ids ")
    void getListOfDocumentsWithContent() throws Exception {
      // given

      // when
      mockMvc
        .perform(get("/api/documentation-units"))
        // then
        .andExpect(jsonPath("$").isNotEmpty())
        .andExpect(
          jsonPath("$.paginatedDocumentUnitListElements.content[0].id").value(
            "11111111-1657-4085-ae2a-993a04c27f6b"
          )
        )
        .andExpect(
          jsonPath(".paginatedDocumentUnitListElements.content[1].id").value(
            "22222222-1657-4085-ae2a-993a04c27f6b"
          )
        );
    }
  }
}
