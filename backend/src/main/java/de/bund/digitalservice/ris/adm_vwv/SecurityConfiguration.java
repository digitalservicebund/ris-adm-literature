package de.bund.digitalservice.ris.adm_vwv;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                customizer ->
                        customizer
                                .requestMatchers(
                                        "/actuator/**",
                                        "/api/**")
                                .permitAll())
                .csrf(AbstractHttpConfigurer::disable);
//                                .anyRequest()
//                                .authenticated());

        return http.build();
    }

}
