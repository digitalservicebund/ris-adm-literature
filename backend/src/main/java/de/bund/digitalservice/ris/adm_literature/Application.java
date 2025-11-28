package de.bund.digitalservice.ris.adm_literature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.cloud.kubernetes.commons.KubernetesCommonsAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The RIS ADM/VwV backend application
 */
@SpringBootApplication(exclude = FlywayAutoConfiguration.class)
@Import(KubernetesCommonsAutoConfiguration.class)
@EnableScheduling
@EnableResilientMethods
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
