package com.example.winter_olympics.config;

import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, exception) ->
                                response.sendError(HttpStatus.FORBIDDEN.value()))
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Authentication
                        .requestMatchers(
                                "/api/auth/**",
                                "/error"
                        ).permitAll()

                        // Public Olympic information
                        .requestMatchers(
                                "/api/olympics/**",
                                "/api/slalom-results/ranking/**",
                                "/api/biathlon-results/ranking/**"
                        ).permitAll()

                        // Competition details are public; management remains admin-only
                        .requestMatchers(HttpMethod.GET, "/api/competitions/**").permitAll()

                        // Admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/athletes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/athletes/me").hasAnyRole("ATHLETE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/athletes", "/api/athletes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/countries").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/countries/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/countries/**").hasRole("ADMIN")
                        .requestMatchers(
                                "/api/competitions/**",
                                "/api/slalom-results/**",
                                "/api/biathlon-results/**"
                        ).hasRole("ADMIN")

                        // Authenticated athletes
                        .requestMatchers(
                                "/api/athletes/**",
                                "/api/registrations/**"
                        ).hasAnyRole("ATHLETE", "ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:5173}") String allowedOrigins
    ) {
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        if (origins.stream().anyMatch(origin -> origin.contains("*"))) {
            throw new IllegalStateException(
                    "CORS_ALLOWED_ORIGINS must list explicit origins; wildcard origins are not allowed"
            );
        }
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
