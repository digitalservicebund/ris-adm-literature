package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DocumentTypeController.class)
@Import(SecurityConfiguration.class)
class DocumentTypeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Request GET returns HTTP 200 and two documentTypes with label and value")
  void getDocumentTypes() throws Exception {
    // given

    // when
    mockMvc
      .perform(get("/api/wertetabellen/dokument-typ"))
      // then
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.[0].abbreviation").value("VE"))
      .andExpect(jsonPath("$.[0].name").value("Verwaltungsvereinbarung"))
      .andExpect(jsonPath("$.[1].abbreviation").value("VR"))
      .andExpect(jsonPath("$.[1].name").value("Verwaltungsregelung"));
  }
}
