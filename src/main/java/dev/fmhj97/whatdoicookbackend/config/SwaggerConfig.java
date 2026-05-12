package dev.fmhj97.whatdoicookbackend.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

/*
 * Defines the JWT security scheme for Swagger UI.
 * This allows sending the Bearer token directly from the Swagger interface.
 */
@SecurityScheme(
        name = "bearerAuth", // Name referenced in controllers with @SecurityRequirement
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("What Do I Cook? API")
                        .description("REST API for the What Do I Cook? application")
                        .version("1.0.0")
                );
    }

}
