package de.bund.digitalservice.ris.adm_literature.config.multischema;

import lombok.experimental.UtilityClass;

/**
 * Utility class to store the current {@link SchemaType} in a {@link ThreadLocal}.
 * This allows the {@link SchemaRoutingDataSource} to determine which
 * schema to use for the current thread's transaction.
 */
@UtilityClass
public final class SchemaContextHolder {

  private static final ThreadLocal<SchemaType> CONTEXT = new ThreadLocal<>();

  /**
   * Sets the {@link SchemaType} for the current thread.
   *
   * @param schema The schema to set (must not be null).
   */
  public static void setSchema(SchemaType schema) {
    if (schema == null) {
      throw new NullPointerException("SchemaType cannot be null");
    }
    CONTEXT.set(schema);
  }

  /**
   * Retrieves the {@link SchemaType} for the current thread.
   *
   * @return The current schema, or null if not set.
   */
  public static SchemaType getSchema() {
    return CONTEXT.get();
  }

  /**
   * Clears the {@link SchemaType} from the current thread.
   * This should be called after the request is complete.
   */
  public static void clear() {
    CONTEXT.remove();
  }
}
