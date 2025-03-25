package de.bund.digitalservice.ris.adm_vwv.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the OpenAPI documentation generator
 */
@Configuration
public class SwaggerConfiguration {

  /**
   * OpenAPI bean
   *
   * @return Configured OpenAPI object with application details
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(
        new Info()
          .title("RIS ADM / VwV REST API")
          .version("0.0.1-SNAPSHOT")
          .description(
            "API description for NeuRIS Administrative directives / Verwaltungsvorschriften"
          )
          .license(
            new License()
              .name("GPL-3.0 license")
              .url("https://github.com/digitalservicebund/ris-adm-vwv/blob/main/LICENSE")
          )
      );
  }
}
