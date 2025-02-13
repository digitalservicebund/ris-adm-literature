package de.bund.digitalservice.ris.adm_vwv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize ->
      authorize
        .requestMatchers(HttpMethod.GET, "/actuator/**")
        .permitAll()
        .requestMatchers(HttpMethod.GET, "/api/swagger-ui/**", "/api/v3/*")
        .permitAll()
        .requestMatchers("/api/documentation-units/**")
        .permitAll()
        .anyRequest()
        .authenticated()
    );
    http.csrf(AbstractHttpConfigurer::disable);
    http.cors(AbstractHttpConfigurer::disable);
    return http.build();
  }
}
