package de.bund.digitalservice.ris.adm_literature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The RIS ADM/VwV backend application
 */
@SpringBootApplication(exclude = FlywayAutoConfiguration.class)
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
