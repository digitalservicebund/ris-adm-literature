package de.bund.digitalservice.ris.adm_literature.lookup_tables.court;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.bund.digitalservice.ris.adm_literature.config.security.SecurityConfiguration;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import de.bund.digitalservice.ris.adm_literature.page.TestPage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CourtController.class)
@Import(SecurityConfiguration.class)
@WithMockUser(roles = "adm_user")
class CourtControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CourtService courtService;

  @Test
  @DisplayName("GET returns HTTP 200 and a JSON with two courts with type, and location")
  void getCourts() throws Exception {
    // given
    var court1 = new Court(UUID.randomUUID(), "AG", "Aachen");
    var court2 = new Court(UUID.randomUUID(), "Berufsgericht für Architekten", "Bremen");
    String searchTerm = "a";
    given(
      courtService.findCourts(
        new CourtQuery(searchTerm, new QueryOptions(0, 2, "type", Sort.Direction.ASC, true))
      )
    ).willReturn(TestPage.create(List.of(court1, court2)));

    // when
    mockMvc
      .perform(
        get("/api/lookup-tables/courts").param("searchTerm", searchTerm).param("pageSize", "2")
      )
      // then
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.courts[0].type").value(court1.type()))
      .andExpect(jsonPath("$.courts[0].location").value(court1.location()))
      .andExpect(jsonPath("$.courts[1].type").value(court2.type()))
      .andExpect(jsonPath("$.courts[1].location").value(court2.location()));
  }
}
