package de.bund.digitalservice.ris.adm_literature.config.multischema;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * A custom {@link AbstractRoutingDataSource} that determines the
 * appropriate {@link javax.sql.DataSource} at runtime based on the
 * current thread's schema context.
 */
public class SchemaRoutingDataSource extends AbstractRoutingDataSource {

  /**
   * Determines which {@link javax.sql.DataSource} to use by retrieving the
   * current {@link SchemaType} from the {@link SchemaContextHolder}.
   *
   * @return The lookup key ({@link SchemaType}) for the target DataSource.
   */
  @Override
  protected Object determineCurrentLookupKey() {
    return SchemaContextHolder.getSchema();
  }
}
