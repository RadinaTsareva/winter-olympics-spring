package com.example.winter_olympics.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI winterOlympicsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Winter Olympics API")
                        .description("REST API for Winter Olympics competitions, athletes, registrations, results, rankings, medals, and statistics."))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter a JWT access token.")));
    }
}
