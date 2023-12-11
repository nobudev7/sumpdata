package com.example.sumpdata;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApiDocumentConfiguration {
    @Value("${api.version}")
    private String apiVersion;

    @Bean
    public OpenAPI sumpdataOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("REST API Server for IoT - Sump Water Level Application")
                        .description("A showcase of a Java Spring Boot app that manages IoT device data via REST API. " +
                                "As a real-world application, this project is intended to collect water level data from " +
                                "Raspi-Sump - a water level monitor using a Raspberry Pi.")
                        .version(apiVersion)
                        .license(new License().name("MIT License").url("https://opensource.org/license/mit/")))
                .servers(List.of(new Server().description("local server").url("http://localhost:8080")));
    }
}
