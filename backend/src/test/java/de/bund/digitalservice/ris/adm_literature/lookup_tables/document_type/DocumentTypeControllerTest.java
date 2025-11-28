package de.bund.digitalservice.ris.adm_literature.lookup_tables.document_type;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.bund.digitalservice.ris.adm_literature.config.security.SecurityConfiguration;
import de.bund.digitalservice.ris.adm_literature.document_category.DocumentCategory;
import de.bund.digitalservice.ris.adm_literature.page.QueryOptions;
import de.bund.digitalservice.ris.adm_literature.page.TestPage;
import java.util.List;
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

@WebMvcTest(controllers = DocumentTypeController.class)
@WithMockUser(roles = "adm_user")
@Import(SecurityConfiguration.class)
class DocumentTypeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DocumentTypeService documentTypeService;

  @Test
  @DisplayName("GET returns HTTP 200 and a JSON with two documentTypes with abbreviation and name")
  void getDocumentTypes() throws Exception {
    // given
    String searchTerm = "verwaltungs";
    given(
      documentTypeService.findDocumentTypes(
        new DocumentTypeQuery(
          searchTerm,
          DocumentCategory.VERWALTUNGSVORSCHRIFTEN,
          new QueryOptions(0, 2, "name", Sort.Direction.ASC, true)
        )
      )
    ).willReturn(
      TestPage.create(
        List.of(
          new DocumentType("VE", "Verwaltungsvereinbarung"),
          new DocumentType("VR", "Verwaltungsregelung")
        )
      )
    );

    // when
    mockMvc
      .perform(
        get("/api/lookup-tables/document-types")
          .param("searchTerm", searchTerm)
          .param("documentCategory", "VERWALTUNGSVORSCHRIFTEN")
          .param("pageSize", "2")
      )
      // then
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.documentTypes[0].abbreviation").value("VE"))
      .andExpect(jsonPath("$.documentTypes[0].name").value("Verwaltungsvereinbarung"))
      .andExpect(jsonPath("$.documentTypes[1].abbreviation").value("VR"))
      .andExpect(jsonPath("$.documentTypes[1].name").value("Verwaltungsregelung"));
  }
}
