package de.bund.digitalservice.ris.adm_literature.config.multischema;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the individual DataSources for each schema.
 * This class is responsible for creating the 'adm' and 'lit' DataSource beans,
 * which are later used by the {@link SchemaRoutingDataSource}.
 */
@Configuration
@Slf4j
public class DataSourceConfig {

  /**
   * Loads the data source properties for the 'adm' schema.
   *
   * @return Properties bound from 'spring.datasource.adm'.
   */
  @Bean
  @ConfigurationProperties("spring.datasource.adm")
  public DataSourceProperties admDataSourceProperties() {
    return new DataSourceProperties();
  }

  /**
   * Loads the data source properties for the 'lit' schema.
   *
   * @return Properties bound from 'spring.datasource.lit'.
   */
  @Bean
  @ConfigurationProperties("spring.datasource.literature")
  public DataSourceProperties literatureDataSourceProperties() {
    return new DataSourceProperties();
  }

  /**
   * Creates the actual {@link DataSource} bean for the 'adm' schema.
   *
   * @return The configured 'adm' DataSource.
   */
  @Bean(name = "admDataSource")
  public DataSource admDataSource() {
    DataSourceProperties dataSourceProperties = admDataSourceProperties();
    log.info("Adm database url: '{}'", dataSourceProperties.getUrl());
    log.info("Adm database user: '{}'", dataSourceProperties.getUsername());
    return dataSourceProperties.initializeDataSourceBuilder().build();
  }

  /**
   * Creates the actual {@link DataSource} bean for the 'lit' schema.
   *
   * @return The configured 'lit' DataSource.
   */
  @Bean(name = "litDataSource")
  public DataSource litDataSource() {
    DataSourceProperties dataSourceProperties = literatureDataSourceProperties();
    log.info("Literature database url: '{}'", dataSourceProperties.getUrl());
    log.info("Literature database user: '{}'", dataSourceProperties.getUsername());
    return dataSourceProperties.initializeDataSourceBuilder().build();
  }
}
