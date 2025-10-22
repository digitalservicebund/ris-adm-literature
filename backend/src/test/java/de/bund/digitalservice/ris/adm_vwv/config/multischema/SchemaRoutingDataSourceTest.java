package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    assertEquals(SchemaType.ADM, ds.determineCurrentLookupKey());

    SchemaContextHolder.setSchema(SchemaType.LIT);
    assertEquals(SchemaType.LIT, ds.determineCurrentLookupKey());
  }
}
