package de.bund.digitalservice.ris.adm_vwv.config.multischema;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

  private static final String MIGRATION_LOCATION = "classpath:db/migration";

  /**
   * Defines the Flyway configuration for the ADM schema.
   * This is NOT a bean, just a private helper method.
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
   * This bean is the ONLY migration-related bean.
   * It calls the private methods to create and run the migrations.
   */
  @Bean
  public Object flywayInitializer(
    @Qualifier("admDataSource") DataSource admDataSource,
    @Value("${database.adm_schema}") String admSchema,
    @Qualifier("litDataSource") DataSource litDataSource,
    @Value("${database.lit_schema}") String litSchema
  ) {
    // Call the private methods to get the Flyway instances and migrate
    createFlywayAdm(admDataSource, admSchema).migrate();
    createFlywayLit(litDataSource, litSchema).migrate();

    return new Object(); // Return a dummy object
  }
}
