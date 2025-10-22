package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SchemaContextHolderTest {

  @AfterEach
  void tearDown() {
    SchemaContextHolder.clear();
  }

  @Test
  void setAndGetSchema_returnsTheSameValue() {
    SchemaContextHolder.setSchema(SchemaType.ADM);
    assertEquals(SchemaType.ADM, SchemaContextHolder.getSchema());

    SchemaContextHolder.setSchema(SchemaType.LIT);
    assertEquals(SchemaType.LIT, SchemaContextHolder.getSchema());
  }

  @Test
  void clear_removesSchemaFromThreadLocal() {
    SchemaContextHolder.setSchema(SchemaType.ADM);
    assertNotNull(SchemaContextHolder.getSchema());

    SchemaContextHolder.clear();
    assertNull(SchemaContextHolder.getSchema());
  }

  @Test
  void setSchema_null_throwsNPE() {
    assertThrows(NullPointerException.class, () -> SchemaContextHolder.setSchema(null));
  }
}
