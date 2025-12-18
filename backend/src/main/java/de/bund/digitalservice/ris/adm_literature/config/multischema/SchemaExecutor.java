package de.bund.digitalservice.ris.adm_literature.config.multischema;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SchemaExecutor {

  public <T> T executeInSchema(SchemaType schema, Supplier<T> action) {
    SchemaType originalSchema = SchemaContextHolder.getSchema();
    try {
      SchemaContextHolder.setSchema(schema);
      return action.get();
    } finally {
      if (originalSchema != null) {
        SchemaContextHolder.setSchema(originalSchema);
      } else {
        SchemaContextHolder.clear();
      }
    }
  }
}
