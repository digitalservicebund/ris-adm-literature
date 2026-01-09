package de.bund.digitalservice.ris.adm_literature.config.multischema;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

/**
 * Configures the primary {@link DataSource} bean as a {@link SchemaRoutingDataSource}.
 * This bean is responsible for directing JPA and JDBC operations to the correct
 * schema ('adm' or 'lit') based on the context set in {@link SchemaContextHolder}.
 */
@Configuration
public class RoutingDataSourceConfig {

  /**
   * Creates the primary {@link DataSource} bean used by the application.
   * <p>
   * This bean is a {@link SchemaRoutingDataSource} that holds references to the
   * individual 'adm' and 'lit' DataSources. It uses {@link SchemaType} as a
   * lookup key to determine which actual DataSource to use.
   * <p>
   * It is marked {@link Primary} so that Spring Boot's auto-configuration (like JPA)
   * picks it up as the main DataSource.
   * <p>
   * It {@link DependsOn} "flywayInitializer" to ensure that database migrations
   * have completed on *both* schemas before this bean is created and used.
   *
   * @param admDataSource The qualified 'adm' {@link DataSource} bean.
   * @param litDataSource The qualified 'lit' {@link DataSource} bean.
   * @return The primary, routing {@link DataSource}.
   */
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
    targetDataSources.put(SchemaType.LITERATURE, litDataSource);
    routingDataSource.setTargetDataSources(targetDataSources);

    // Set 'adm' as the default fallback
    routingDataSource.setDefaultTargetDataSource(admDataSource);

    return routingDataSource;
  }
}
