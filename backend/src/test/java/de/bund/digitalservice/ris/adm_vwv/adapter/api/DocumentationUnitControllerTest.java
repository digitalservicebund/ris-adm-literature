package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitPort;
import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DocumentationUnitController.class)
@Import(SecurityConfiguration.class)
class DocumentationUnitControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DocumentationUnitPort documentationUnitPort;

  @Test
  @DisplayName("Request POST returns HTTP 201 and data from mocked documentation unit port")
  void create() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    given(documentationUnitPort.create())
      .willReturn(new DocumentationUnit("KSNR054920707", id, null));

    // when
    mockMvc
      .perform(post("/api/documentation-units"))
      // then
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(id.toString()))
      .andExpect(jsonPath("$.documentNumber").value("KSNR054920707"));
  }

  @Test
  @DisplayName("Request PUT returns HTTP 200 and data from mocked documentation unit port")
  void update() throws Exception {
    // given
    String documentNumber = "KSNR054920707";
    String json = "{\"test\":\"content\"}";
    given(documentationUnitPort.update(documentNumber, json))
      .willReturn(Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), json)));

    // when
    mockMvc
      .perform(
        put("/api/documentation-units/{documentNumber}", documentNumber)
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
    String documentNumber = "KSNR054920707";
    String json = "{\"test\":\"unsuccessful\"}";
    given(documentationUnitPort.update(documentNumber, json)).willReturn(Optional.empty());

    // when
    mockMvc
      .perform(
        put("/api/documentation-units/{documentNumber}", documentNumber)
          .content(json)
          .contentType(MediaType.APPLICATION_JSON)
      )
      // then
      .andExpect(status().isNotFound());
  }
}
