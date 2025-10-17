package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.adm")
  public DataSourceProperties admDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.lit")
  public DataSourceProperties litDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean(name = "admDataSource")
  public DataSource admDataSource() {
    return admDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean(name = "litDataSource")
  public DataSource litDataSource() {
    return litDataSourceProperties().initializeDataSourceBuilder().build();
  }
}
