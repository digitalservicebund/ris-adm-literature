package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SchemaRoutingDataSourceTest {

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
  }

  @Test
  void determineCurrentLookupKey_returnsSchemaFromContext() {
    SchemaRoutingDataSource ds = new SchemaRoutingDataSource();

    SchemaContextHolder.setSchema(SchemaType.ADM);
    assertThat(ds.determineCurrentLookupKey()).isEqualTo(SchemaType.ADM);

    SchemaContextHolder.setSchema(SchemaType.LIT);
    assertThat(ds.determineCurrentLookupKey()).isEqualTo(SchemaType.LIT);
  }
}
