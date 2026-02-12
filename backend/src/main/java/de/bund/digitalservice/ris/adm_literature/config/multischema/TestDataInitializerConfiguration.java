package de.bund.digitalservice.ris.adm_literature.config.multischema;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Test data initializer configuration which feeds test data for e2e test with the
 * right datasource. This bean is only active with profiles "default" and "test" which are
 * used on local starting and for e2e tests in the build pipeline.
 */
@Configuration
@Profile({ "default", "test" })
public class TestDataInitializerConfiguration {

  /**
   * Creates a data source initializer for schema adm.
   * @param admDataSource The adm data source
   * @return Instance of data source initializer
   */
  @Bean
  public DataSourceInitializer admInitializer(
    @Qualifier("admDataSource") DataSource admDataSource
  ) {
    return createInitializer(admDataSource, "data-adm.sql");
  }

  /**
   * Creates a data source initializer for schema literature.
   * @param litDataSource The literature data source
   * @return Instance of data source initializer
   */
  @Bean
  public DataSourceInitializer literatureInitializer(
    @Qualifier("litDataSource") DataSource litDataSource
  ) {
    return createInitializer(litDataSource, "data-literature.sql");
  }

  /**
   * Creates a data source initializer for schema literature.
   * @param referencesDataSource The literature data source
   * @return Instance of data source initializer
   */
  @Bean
  public DataSourceInitializer referencesInitializer(
    @Qualifier("referencesDataSource") DataSource referencesDataSource
  ) {
    return createInitializer(referencesDataSource, "data-references.sql");
  }

  private DataSourceInitializer createInitializer(DataSource dataSource, String scriptPath) {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(new ClassPathResource(scriptPath));
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(populator);
    return initializer;
  }
}
