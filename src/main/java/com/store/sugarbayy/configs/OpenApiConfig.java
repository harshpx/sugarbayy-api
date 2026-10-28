package com.store.sugarbayy.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI applicationOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("SugarBayy API")
                .description("API documentation for SugarBayy")
                .version("v1")
                .contact(new Contact().name("SugarBayy")));
    }
}
