package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;

@WebMvcTest(controllers = DokumentTypController.class)
@Import(SecurityConfiguration.class)
class DokumentTypControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Request GET returns HTTP 200")
  void find() throws Exception {
    // given

    // when
    mockMvc
      .perform(get("/api/wertetabellen/dokument-typ"))
      // then
      .andExpect(status().isOk())
      // TODO: test for JSON content
  }
}
