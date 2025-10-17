package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class SchemaContextHolder {

  private static final ThreadLocal<SchemaType> CONTEXT = new ThreadLocal<>();

  public static void setSchema(SchemaType schema) {
    if (schema == null) {
      throw new NullPointerException("SchemaType cannot be null");
    }
    CONTEXT.set(schema);
  }

  public static SchemaType getSchema() {
    return CONTEXT.get();
  }

  public static void clear() {
    CONTEXT.remove();
  }
}
