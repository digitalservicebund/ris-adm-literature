package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the individual DataSources for each schema.
 * This class is responsible for creating the 'adm' and 'lit' DataSource beans,
 * which are later used by the {@link SchemaRoutingDataSource}.
 */
@Configuration
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
    return admDataSourceProperties().initializeDataSourceBuilder().build();
  }

  /**
   * Creates the actual {@link DataSource} bean for the 'lit' schema.
   *
   * @return The configured 'lit' DataSource.
   */
  @Bean(name = "litDataSource")
  public DataSource litDataSource() {
    return literatureDataSourceProperties().initializeDataSourceBuilder().build();
  }
}
