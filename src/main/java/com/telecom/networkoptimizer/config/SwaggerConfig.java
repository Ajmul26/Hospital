package com.telecom.networkoptimizer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Telecommunications Network Optimizer API")
                        .description("Comprehensive API for managing and optimizing telecommunications network infrastructure. " +
                                   "This system enhances network efficiency and performance through advanced algorithms, " +
                                   "real-time monitoring, and intelligent resource allocation.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Network Optimization Team")
                                .email("support@telecom-optimizer.com")
                                .url("https://telecom-optimizer.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.telecom-optimizer.com")
                                .description("Production Server")))
                .tags(List.of(
                        new Tag()
                                .name("Network Nodes")
                                .description("Operations for managing telecommunications network nodes including routers, switches, base stations, and other infrastructure components"),
                        new Tag()
                                .name("Network Metrics")
                                .description("APIs for recording, retrieving, and analyzing network performance metrics including latency, throughput, packet loss, and resource utilization"),
                        new Tag()
                                .name("Network Optimization")
                                .description("Advanced optimization algorithms for load balancing, bandwidth optimization, route optimization, capacity planning, and cost reduction"),
                        new Tag()
                                .name("Analytics & Reporting")
                                .description("Comprehensive analytics, performance trends, and reporting capabilities for network health and optimization insights"),
                        new Tag()
                                .name("System Management")
                                .description("System administration, health checks, configuration management, and monitoring endpoints")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token authentication"))
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Basic HTTP authentication")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}