package com.university.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the REST API.
 *
 * This is a stateless REST API that uses JSON request bodies (not HTML forms).
 * CSRF protection is only relevant for browser-based form submissions using
 * session cookies. REST APIs authenticate via tokens (e.g., JWT) — not cookies.
 *
 * Therefore, CSRF is intentionally disabled here per OWASP REST Security guidelines:
 * https://cheatsheetseries.owasp.org/cheatsheets/REST_Security_Cheat_Sheet.html
 *
 * SessionCreationPolicy.STATELESS ensures no HTTP session is created,
 * which eliminates the attack surface CSRF targets.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF: safe for stateless REST APIs using JSON (not HTML form POST)
            .csrf(csrf -> csrf.disable())

            // Do not create or use HTTP sessions (stateless REST API)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Allow all requests (add authentication rules here if you add login later)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
