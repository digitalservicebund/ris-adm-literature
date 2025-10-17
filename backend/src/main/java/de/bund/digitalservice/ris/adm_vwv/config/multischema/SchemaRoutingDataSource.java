package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class SchemaRoutingDataSource extends AbstractRoutingDataSource {

  @Override
  protected Object determineCurrentLookupKey() {
    return SchemaContextHolder.getSchema();
  }
}
