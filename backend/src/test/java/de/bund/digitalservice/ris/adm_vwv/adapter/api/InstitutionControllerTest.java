package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InstitutionController.class)
@Import(SecurityConfiguration.class)
class InstitutionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LookupTablesPort lookupTablesPort;

  @Test
  @DisplayName("GET returns HTTP 200 and a JSON with two institutions with name, regions, and type")
  void getInstitutions() throws Exception {
    // given
    String searchTerm = "jurpn";
    given(
      lookupTablesPort.findInstitutions(
        new InstitutionQuery(searchTerm, new QueryOptions(0, 2, "name", Sort.Direction.ASC, true))
      )
    ).willReturn(
      new PageImpl<>(
        List.of(
          new Institution(
            "Erste Jurpn",
            "Jurpn 1",
            InstitutionType.LEGAL_ENTITY,
            List.of(new Region("AA", null), new Region("BB", null))
          ),
          new Institution("Zweite Jurpn", "Jurpn 2", InstitutionType.LEGAL_ENTITY, List.of())
        )
      )
    );

    // when
    mockMvc
      .perform(
        get("/api/lookup-tables/institutions")
          .param("searchTerm", searchTerm)
          .param("pageSize", "2")
      )
      // then
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.institutions[0].name").value("Erste Jurpn"))
      .andExpect(jsonPath("$.institutions[0].type").value("LEGAL_ENTITY"))
      .andExpect(jsonPath("$.institutions[0].regions[0].code").value("AA"))
      .andExpect(jsonPath("$.institutions[0].regions[1].code").value("BB"))
      .andExpect(jsonPath("$.institutions[1].name").value("Zweite Jurpn"))
      .andExpect(jsonPath("$.institutions[1].type").value("LEGAL_ENTITY"))
      .andExpect(jsonPath("$.institutions[1].regions").isEmpty());
  }
}
