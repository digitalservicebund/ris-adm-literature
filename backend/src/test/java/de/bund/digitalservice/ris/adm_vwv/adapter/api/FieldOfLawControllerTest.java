package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLaw;
import de.bund.digitalservice.ris.adm_vwv.application.FieldOfLawQuery;
import de.bund.digitalservice.ris.adm_vwv.application.LookupTablesPort;
import de.bund.digitalservice.ris.adm_vwv.application.PageQuery;
import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FieldOfLawController.class)
@Import(SecurityConfiguration.class)
class FieldOfLawControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LookupTablesPort lookupTablesPort;

  @Test
  @DisplayName("GET returns HTTP 200 and a JSON with one field of law child")
  void getFieldsOfLaw() throws Exception {
    // given
    given(lookupTablesPort.findFieldsOfLawChildren("PR-01")).willReturn(
      List.of(
        new FieldOfLaw(
          UUID.randomUUID(),
          false,
          "PR-01-05",
          "Phantasierecht speziell",
          List.of(),
          List.of(),
          List.of(),
          null
        )
      )
    );

    // when
    mockMvc
      .perform(get("/api/lookup-tables/fields-of-law/{identifier}/children", "PR-01"))
      // then
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.fieldsOfLaw[0].identifier").value("PR-01-05"))
      .andExpect(jsonPath("$.fieldsOfLaw[0].text").value("Phantasierecht speziell"));
  }

  @Test
  @DisplayName("GET returns HTTP 200 and a JSON with one field of law root (parent)")
  void getFieldsOfLawParents() throws Exception {
    // given
    given(lookupTablesPort.findFieldsOfLawParents()).willReturn(
      List.of(
        new FieldOfLaw(
          UUID.randomUUID(),
          false,
          "AR",
          "Phantasierecht",
          List.of(),
          List.of(),
          List.of(),
          null
        )
      )
    );

    // when
    mockMvc
      .perform(get("/api/lookup-tables/fields-of-law/root/children"))
      // then
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.fieldsOfLaw[0].identifier").value("AR"))
      .andExpect(jsonPath("$.fieldsOfLaw[0].text").value("Phantasierecht"));
  }

  @Test
  @DisplayName("GET returns HTTP 200 and a JSON with one field of law")
  void getTreeForFieldOfLaw() throws Exception {
    // given
    given(lookupTablesPort.findFieldOfLaw("AR")).willReturn(
      Optional.of(
        new FieldOfLaw(
          UUID.randomUUID(),
          false,
          "AR",
          "Phantasierecht",
          List.of(),
          List.of(),
          List.of(),
          null
        )
      )
    );

    // when
    mockMvc
      .perform(get("/api/lookup-tables/fields-of-law/{identifier}", "AR"))
      // then
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.identifier").value("AR"))
      .andExpect(jsonPath("$.text").value("Phantasierecht"));
  }

  @Test
  @DisplayName("GET returns HTTP 404 for unknown identifier")
  void getTreeForFieldOfLaw_notFound() throws Exception {
    // given
    given(lookupTablesPort.findFieldOfLaw("BR-123456")).willReturn(Optional.empty());

    // when
    mockMvc
      .perform(get("/api/lookup-tables/fields-of-law/{identifier}", "BR-123456"))
      // then
      .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET returns HTTP 200 and a JSON with one field of law")
  void findFieldsOfLaw() throws Exception {
    // given
    FieldOfLawQuery query = new FieldOfLawQuery(
      "PR-05",
      "arbeit",
      null,
      new PageQuery(0, 10, "identifier", Sort.Direction.ASC, true)
    );
    given(lookupTablesPort.findFieldsOfLaw(query)).willReturn(
      new PageImpl<>(
        List.of(
          new FieldOfLaw(
            UUID.randomUUID(),
            false,
            "PR-05",
            "Phantasierecht",
            List.of(),
            List.of(),
            List.of(),
            null
          )
        )
      )
    );

    // when
    mockMvc
      .perform(
        get("/api/lookup-tables/fields-of-law")
          .param("identifier", "PR-05")
          .param("text", "arbeit")
          .param("sortBy", "identifier")
          .param("size", "10")
      )
      // then
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.fieldsOfLaw[0].identifier").value("PR-05"))
      .andExpect(jsonPath("$.fieldsOfLaw[0].text").value("Phantasierecht"));
  }
}
