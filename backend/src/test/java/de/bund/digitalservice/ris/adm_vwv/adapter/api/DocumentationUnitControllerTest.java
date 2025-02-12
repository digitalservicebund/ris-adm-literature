package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_vwv.application.CreateDocumentationUnitPort;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = DocumentationUnitController.class)
@Import(SecurityConfiguration.class)
class DocumentationUnitControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CreateDocumentationUnitPort createDocumentationUnitPort;

  @Test
  void create() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    given(createDocumentationUnitPort.create())
      .willReturn(new DocumentationUnit("KSNR054920707", id));

    // when
    mockMvc
      .perform(MockMvcRequestBuilders.post("/api/documentation-units"))
      // then
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.documentNumber").value("KSNR054920707"));
  }
}
