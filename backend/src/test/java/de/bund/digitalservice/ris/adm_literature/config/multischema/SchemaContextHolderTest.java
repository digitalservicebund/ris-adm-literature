package de.bund.digitalservice.ris.adm_literature.config.multischema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.ADM);

    SchemaContextHolder.setSchema(SchemaType.LITERATURE);
    assertThat(SchemaContextHolder.getSchema()).isEqualTo(SchemaType.LITERATURE);
  }

  @Test
  void clear_removesSchemaFromThreadLocal() {
    SchemaContextHolder.setSchema(SchemaType.ADM);
    assertThat(SchemaContextHolder.getSchema()).isNotNull();

    SchemaContextHolder.clear();
    assertThat(SchemaContextHolder.getSchema()).isNull();
  }

  @Test
  void setSchema_null_throwsNPE() {
    assertThatThrownBy(() -> SchemaContextHolder.setSchema(null)).isInstanceOf(
      NullPointerException.class
    );
  }
}
