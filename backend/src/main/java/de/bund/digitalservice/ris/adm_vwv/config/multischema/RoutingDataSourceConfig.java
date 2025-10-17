package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@Configuration
public class RoutingDataSourceConfig {

  @Bean
  @Primary
  @DependsOn("flywayInitializer")
  public DataSource dataSource(
    @Qualifier("admDataSource") DataSource admDataSource,
    @Qualifier("litDataSource") DataSource litDataSource
  ) {
    SchemaRoutingDataSource routingDataSource = new SchemaRoutingDataSource();

    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put(SchemaType.ADM, admDataSource);
    targetDataSources.put(SchemaType.LIT, litDataSource);
    routingDataSource.setTargetDataSources(targetDataSources);

    routingDataSource.setDefaultTargetDataSource(admDataSource);

    return routingDataSource;
  }
}
