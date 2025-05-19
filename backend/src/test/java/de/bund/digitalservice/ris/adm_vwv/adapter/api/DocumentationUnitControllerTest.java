package de.bund.digitalservice.ris.adm_vwv.adapter.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnit;
import de.bund.digitalservice.ris.adm_vwv.application.DocumentationUnitPort;
import de.bund.digitalservice.ris.adm_vwv.config.SecurityConfiguration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
  class GetListOfDocumentUnits {

    @Nested
    class unpaginatedListOfDocumentUnits {

      @Test
      void returnListOfDocumentsUnpaginated() throws Exception {
        // given

        // when
        mockMvc
          .perform(get("/api/documentation-units"))
          // then
          .andExpect(jsonPath("$.documentationUnitsOverview").exists())
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].id").value(
              "11111111-1657-4085-ae2a-993a04c27f6b"
            )
          )
          .andExpect(
            jsonPath(".documentationUnitsOverview[1].id").value(
              "22222222-1657-4085-ae2a-993a04c27f6b"
            )
          )
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].documentNumber").value(
              "sample documentNumber 1"
            )
          )
          .andExpect(
            jsonPath("$.documentationUnitsOverview[1].documentNumber").value(
              "sample documentNumber 2"
            )
          )
          .andExpect(jsonPath("$.documentationUnitsOverview[0].zitierdatum").value("2011-11-11"))
          .andExpect(jsonPath("$.documentationUnitsOverview[1].zitierdatum").value("2011-11-11"))
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
      @DisplayName("return array of Fundstellen with ids and Zitatstellen")
      void getListOfDocumentsWithFundstellen() throws Exception {
        // given

        // when
        mockMvc
          .perform(get("/api/documentation-units"))
          // then
          .andExpect(jsonPath("$.documentationUnitsOverview[0].fundstellen").isNotEmpty())
          // ids
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].fundstellen[0].id").value(
              "11111111-1fd3-4fb8-bc1d-9751ad192665"
            )
          )
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].fundstellen[1].id").value(
              "22222222-1fd3-4fb8-bc1d-9751ad192665"
            )
          )
          // Zitatstellen
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].fundstellen[0].zitatstelle").value(
              "zitatstelle 1"
            )
          )
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].fundstellen[1].zitatstelle").value(
              "zitatstelle 2"
            )
          );
      }

      @Test
      @DisplayName("return Periodikum with id, title, subtitle and abbreviation")
      void getListOfDocumentsWithFundstellenAndPeriodika() throws Exception {
        // given

        // when
        mockMvc
          .perform(get("/api/documentation-units"))
          // then
          .andExpect(jsonPath("$.documentationUnitsOverview[0].fundstellen[0].periodikum").exists())
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].fundstellen[0].periodikum.id").value(
              "periodikum id 1"
            )
          )
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].fundstellen[0].periodikum.title").value(
              "periodikum title 1"
            )
          )
          .andExpect(
            jsonPath("$.documentationUnitsOverview[0].fundstellen[0].periodikum.subtitle").value(
              "periodikum subtitle 1"
            )
          )
          .andExpect(
            jsonPath(
              "$.documentationUnitsOverview[0].fundstellen[0].periodikum.abbreviation"
            ).value("p.abbrev.1")
          );
      }
    }
  }

  @Nested
  class PaginatedListOfDocumentUnits {

    @Test
    @DisplayName("returns HTTP 200, uuids, Dokumentnummern, Zitierdaten, Langüberschriften")
    void getListOfDocumentsSuccess() throws Exception {
      // given

      // when
      mockMvc
        .perform(get("/api/documentation-units"))
        // then
        .andExpect(status().isOk())
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[0].id").value(
            "11111111-1657-4085-ae2a-993a04c27f6b"
          )
        )
        .andExpect(
          jsonPath(".paginatedDocumentationUnitsOverview.content[1].id").value(
            "22222222-1657-4085-ae2a-993a04c27f6b"
          )
        )
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[0].documentNumber").value(
            "sample documentNumber 1"
          )
        )
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[1].documentNumber").value(
            "sample documentNumber 2"
          )
        )
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[0].zitierdatum").value(
            "2011-11-11"
          )
        )
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[1].zitierdatum").value(
            "2011-11-11"
          )
        )
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[0].langueberschrift").value(
            "Sample Document Title 1"
          )
        )
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[1].langueberschrift").value(
            "Sample Document Title 2"
          )
        );
    }

    @Test
    @DisplayName("return array of Fundstellen with ids and Zitatstellen")
    void getListOfDocumentsWithFundstellen() throws Exception {
      // given

      // when
      mockMvc
        .perform(get("/api/documentation-units"))
        // then
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[0].fundstellen").isNotEmpty()
        )
        // ids
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[0].fundstellen[0].id").value(
            "11111111-1fd3-4fb8-bc1d-9751ad192665"
          )
        )
        .andExpect(
          jsonPath("$.paginatedDocumentationUnitsOverview.content[0].fundstellen[1].id").value(
            "22222222-1fd3-4fb8-bc1d-9751ad192665"
          )
        )
        // Zitatstellen
        .andExpect(
          jsonPath(
            "$.paginatedDocumentationUnitsOverview.content[0].fundstellen[0].zitatstelle"
          ).value("zitatstelle 1")
        )
        .andExpect(
          jsonPath(
            "$.paginatedDocumentationUnitsOverview.content[0].fundstellen[1].zitatstelle"
          ).value("zitatstelle 2")
        );
    }

    @Test
    @DisplayName("return Periodikum with id, title, subtitle and abbreviation")
    void getListOfDocumentsWithFundstellenAndPeriodika() throws Exception {
      // given

      // when
      mockMvc
        .perform(get("/api/documentation-units"))
        // then
        .andExpect(
          jsonPath(
            "$.paginatedDocumentationUnitsOverview.content[0].fundstellen[0].periodikum"
          ).exists()
        )
        .andExpect(
          jsonPath(
            "$.paginatedDocumentationUnitsOverview.content[0].fundstellen[0].periodikum.id"
          ).value("periodikum id 1")
        )
        .andExpect(
          jsonPath(
            "$.paginatedDocumentationUnitsOverview.content[0].fundstellen[0].periodikum.title"
          ).value("periodikum title 1")
        )
        .andExpect(
          jsonPath(
            "$.paginatedDocumentationUnitsOverview.content[0].fundstellen[0].periodikum.subtitle"
          ).value("periodikum subtitle 1")
        )
        .andExpect(
          jsonPath(
            "$.paginatedDocumentationUnitsOverview.content[0].fundstellen[0].periodikum.abbreviation"
          ).value("p.abbrev.1")
        );
    }
  }
}
