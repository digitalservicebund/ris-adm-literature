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
    return http
      .authorizeHttpRequests(authorize ->
        authorize
          // @spotless:off
          .requestMatchers(HttpMethod.HEAD, "/actuator/**").permitAll()
          .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
          .requestMatchers("/api/swagger-ui/**").permitAll()
          .requestMatchers("/api/documentation-units/**").permitAll()
          // @spotless:on
          .anyRequest()
          .authenticated()
      )
      .csrf(AbstractHttpConfigurer::disable)
      .cors(AbstractHttpConfigurer::disable)
      .build();
  }
}
