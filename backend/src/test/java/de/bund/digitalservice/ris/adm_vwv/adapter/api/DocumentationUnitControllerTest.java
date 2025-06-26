package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_vwv.application.*;
import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
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
  @DisplayName("Request GET returns HTTP 200 and data from mocked documentation unit port")
  void find() throws Exception {
    // given
    String documentNumber = "KSNR054920707";
    String json = "{\"test\":\"content\"}";
    given(documentationUnitPort.findByDocumentNumber(documentNumber)).willReturn(
      Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), json))
    );

    // when
    mockMvc
      .perform(get("/api/documentation-units/{documentNumber}", documentNumber))
      // then
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.documentNumber").value(documentNumber))
      .andExpect(jsonPath("$.json.test").value("content"));
  }

  @Test
  @DisplayName(
    "Request GET returns HTTP 404 because mocked documentation unit port returns empty optional"
  )
  void find_notFound() throws Exception {
    // given
    String documentNumber = "KSNR000000001";
    given(documentationUnitPort.findByDocumentNumber(documentNumber)).willReturn(Optional.empty());

    // when
    mockMvc
      .perform(get("/api/documentation-units/{documentNumber}", documentNumber))
      // then
      .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Request POST returns HTTP 201 and data from mocked documentation unit port")
  void create() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    given(documentationUnitPort.create()).willReturn(
      new DocumentationUnit("KSNR054920707", id, null)
    );

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
    given(documentationUnitPort.update(documentNumber, json)).willReturn(
      Optional.of(new DocumentationUnit(documentNumber, UUID.randomUUID(), json))
    );

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
    String documentNumber = "KSNR000000001";
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

  @Nested
  class PaginatedListOfDocumentUnits {

    @BeforeEach
    void beforeEach() {
      given(documentationUnitPort.findDocumentationUnitOverviewElements(any())).willReturn(
        TestPage.create(
          List.of(
            new DocumentationUnitOverviewElement(
              UUID.fromString("11111111-1657-4085-ae2a-993a04c27f6b"),
              "KSNR000004711",
              List.of("2011-11-11"),
              "Sample Document Title 1",
              List.of("p.abbrev.1 zitatstelle 1", "p.abbrev.2 zitatstelle 2")
            ),
            new DocumentationUnitOverviewElement(
              UUID.fromString("22222222-1657-4085-ae2a-993a04c27f6b"),
              "KSNR000004712",
              List.of("2011-11-11"),
              "Sample Document Title 2",
              List.of()
            )
          )
        )
      );
    }

    @Test
    @DisplayName("returns HTTP 200, uuids, Dokumentnummern, Zitierdaten, Langüberschriften")
    void findListOfDocumentsSuccess() throws Exception {
      // given

      // when
      mockMvc
        .perform(get("/api/documentation-units"))
        // then
        .andExpect(status().isOk())
        .andExpect(
          jsonPath("$.documentationUnitsOverview[0].id").value(
            "11111111-1657-4085-ae2a-993a04c27f6b"
          )
        )
        .andExpect(
          jsonPath("$.documentationUnitsOverview[1].id").value(
            "22222222-1657-4085-ae2a-993a04c27f6b"
          )
        )
        .andExpect(
          jsonPath("$.documentationUnitsOverview[0].documentNumber").value("KSNR000004711")
        )
        .andExpect(
          jsonPath("$.documentationUnitsOverview[1].documentNumber").value("KSNR000004712")
        )
        .andExpect(jsonPath("$.documentationUnitsOverview[0].zitierdaten[0]").value("2011-11-11"))
        .andExpect(jsonPath("$.documentationUnitsOverview[1].zitierdaten[0]").value("2011-11-11"))
        .andExpect(
          jsonPath("$.documentationUnitsOverview[0].langueberschrift").value(
            "Sample Document Title 1"
          )
        )
        .andExpect(
          jsonPath("$.documentationUnitsOverview[1].langueberschrift").value(
            "Sample Document Title 2"
          )
        );
    }

    @Test
    @DisplayName("return array of Fundstellen")
    void findListOfDocumentsWithFundstellen() throws Exception {
      // given

      // when
      mockMvc
        .perform(get("/api/documentation-units"))
        // then
        .andExpect(jsonPath("$.documentationUnitsOverview[0].fundstellen").isNotEmpty())
        .andExpect(
          jsonPath("$.documentationUnitsOverview[0].fundstellen[0]").value(
            "p.abbrev.1 zitatstelle 1"
          )
        )
        .andExpect(
          jsonPath("$.documentationUnitsOverview[0].fundstellen[1]").value(
            "p.abbrev.2 zitatstelle 2"
          )
        );
    }

    @Test
    @DisplayName("sends search parameters to the application layer")
    void findListOfDocumentsWithSearchParamsSuccess() throws Exception {
      // given
      String documentNumber = "KSNR000004711";
      String langueberschrift = "Sample Document";
      String fundstellen = "p.abbrev.1";
      String zitierdaten = "2011-11-11";

      QueryOptions queryOptions = new QueryOptions(
        0,
        10,
        "documentNumber",
        Sort.Direction.ASC,
        true
      );
      DocumentationUnitQuery expectedQuery = new DocumentationUnitQuery(
        documentNumber,
        langueberschrift,
        fundstellen,
        zitierdaten,
        queryOptions
      );

      given(documentationUnitPort.findDocumentationUnitOverviewElements(expectedQuery)).willReturn(
        TestPage.create(
          List.of(
            new DocumentationUnitOverviewElement(
              UUID.randomUUID(),
              documentNumber,
              List.of(zitierdaten),
              langueberschrift,
              List.of(fundstellen)
            )
          )
        )
      );

      // when
      mockMvc
        .perform(
          get("/api/documentation-units")
            .param("documentNumber", documentNumber)
            .param("langueberschrift", langueberschrift)
            .param("fundstellen", fundstellen)
            .param("zitierdaten", zitierdaten)
        )
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.documentationUnitsOverview[0].documentNumber").value(documentNumber))
        .andExpect(jsonPath("$.documentationUnitsOverview[0].zitierdaten").value(zitierdaten))
        .andExpect(jsonPath("$.documentationUnitsOverview[0].fundstellen").value(fundstellen))
        .andExpect(
          jsonPath("$.documentationUnitsOverview[0].langueberschrift").value(langueberschrift)
        );
    }
  }
}
