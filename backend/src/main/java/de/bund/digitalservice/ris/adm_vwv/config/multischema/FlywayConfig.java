package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Manages the Flyway database migrations for the multi-schema setup.
 * <p>
 * This class provides a manual configuration to replace the default
 * {@link org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration},
 * which was excluded. It ensures that the same set of migrations is
 * applied to both the 'adm' and 'lit' datasources.
 */
@Configuration
public class FlywayConfig {

  private static final String MIGRATION_LOCATION = "classpath:db/migration";

  /**
   * Defines the Flyway configuration for the ADM schema.
   * This is NOT a bean, just a private helper method.
   *
   * @param admDataSource The 'adm' data source.
   * @param admSchema The name of the 'adm' schema.
   * @return A configured {@link Flyway} instance for the 'adm' schema.
   */
  private Flyway createFlywayAdm(DataSource admDataSource, String admSchema) {
    return Flyway.configure()
      .dataSource(admDataSource)
      .schemas(admSchema)
      .locations(MIGRATION_LOCATION)
      .baselineOnMigrate(true)
      .load();
  }

  /**
   * Defines the Flyway configuration for the LIT schema.
   * This is NOT a bean, just a private helper method.
   *
   * @param litDataSource The 'lit' data source.
   * @param litSchema The name of the 'lit' schema.
   * @return A configured {@link Flyway} instance for the 'lit' schema.
   */
  private Flyway createFlywayLit(DataSource litDataSource, String litSchema) {
    return Flyway.configure()
      .dataSource(litDataSource)
      .schemas(litSchema)
      .locations(MIGRATION_LOCATION)
      .baselineOnMigrate(true)
      .load();
  }

  /**
   * Creates and runs the Flyway migrations for both schemas.
   * <p>
   * This bean acts as the trigger for the database migrations. It calls the
   * private helper methods to configure and execute {@link Flyway#migrate()}
   * for both the 'adm' and 'lit' datasources.
   * <p>
   * The {@link RoutingDataSourceConfig} is configured to {@link org.springframework.context.annotation.DependsOn}
   * this bean, ensuring migrations complete before the main routing data source is used by JPA.
   *
   * @param admDataSource The {@link DataSource} for the 'adm' schema.
   * @param admSchema The schema name for 'adm', injected from properties.
   * @param litDataSource The {@link DataSource} for the 'lit' schema.
   * @param litSchema The schema name for 'lit', injected from properties.
   * @return A dummy {@link Object} to satisfy bean creation; its value is not used.
   */
  @Bean
  public Object flywayInitializer(
    @Qualifier("admDataSource") DataSource admDataSource,
    @Value("${adm.database.schema:adm}") String admSchema,
    @Qualifier("litDataSource") DataSource litDataSource,
    @Value("${literature.database.schema:literature}") String litSchema
  ) {
    // Call the private methods to get the Flyway instances and migrate
    createFlywayAdm(admDataSource, admSchema).migrate();
    createFlywayLit(litDataSource, litSchema).migrate();

    return new Object(); // Return a dummy object
  }
}
