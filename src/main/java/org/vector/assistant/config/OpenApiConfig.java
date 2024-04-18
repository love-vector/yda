package org.vector.assistant.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springframework.context.annotation.Configuration;

/**
 * <a href="http://localhost:8081/swagger-ui.html">Swagger-UI</a>
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "YDA"), security = @SecurityRequirement(name = "basicAuth"))
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "basicAuth", scheme = "basic", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {}
