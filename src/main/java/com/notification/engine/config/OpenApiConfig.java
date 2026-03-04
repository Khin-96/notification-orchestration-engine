package com.notification.engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Orchestration Engine API")
                        .version("1.0.0")
                        .description("Multi-channel notification service with intelligent routing, deduplication, and fallback chains")
                        .contact(new Contact()
                                .name("Chris Kinga Hinzano")
                                .email("hinzanno@gmail.com")
                                .url("https://hinzano.dev")));
    }
}
