package com.jarviswuod.improvedbillingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.ExternalDocumentation;

import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

import io.swagger.v3.oas.annotations.servers.Server;

import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

@OpenAPIDefinition(
        info = @Info(
                title = "Improved Billing System API",
                version = "0.0.1",
                description = "REST API for customers, invoices, payments, and billing dashboards.",
                contact = @Contact(
                        name = "Backend Team",
                        email = "team@company.com",
                        url = "https://company.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development Server"),
                @Server(url = "https://api.company.com", description = "Production Server")
        },
        tags = {
                @Tag(name = "Customers", description = "Customer management operations"),
                @Tag(name = "Invoices", description = "Invoice CRUD and overdue queries"),
                @Tag(name = "Payments", description = "Payment creation and invoice status updates"),
                @Tag(name = "Dashboard", description = "Billing summary and reporting endpoints")
        },
        externalDocs = @ExternalDocumentation(
                url = "https://wiki.company.com/api",
                description = "Full API Documentation"
        )
)
@SecuritySchemes({
        @SecurityScheme(
                name = "bearer-jwt",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT",
                description = "JWT token used to authenticate API calls"
        )
})
@SpringBootApplication
@EnableJpaAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}