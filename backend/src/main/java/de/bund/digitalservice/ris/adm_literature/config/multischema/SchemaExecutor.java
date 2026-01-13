package de.bund.digitalservice.ris.adm_literature.config.multischema;

import org.springframework.stereotype.Component;

/**
 * Utility component for managing temporary database schema context switches.
 */
@Component
public class SchemaExecutor {

  /**
   * Executes a functional action within the context of a specific database schema.
   * * <p>Temporarily switches the {@link SchemaContextHolder} to the target schema,
   * performs the action, and ensures the original schema context is restored
   * even if an exception occurs.</p>
   *
   * @param schema The target {@link SchemaType} to switch to.
   * @param action The logic to execute while the target schema is active.
   */
  public void executeInSchema(SchemaType schema, Runnable action) {
    SchemaType originalSchema = SchemaContextHolder.getSchema();
    try {
      SchemaContextHolder.setSchema(schema);
      action.run();
    } finally {
      if (originalSchema != null) {
        SchemaContextHolder.setSchema(originalSchema);
      } else {
        SchemaContextHolder.clear();
      }
    }
  }
}
