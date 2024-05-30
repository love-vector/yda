package ai.yda.framework.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.context.annotation.Configuration;

/**
 * <a href="http://localhost:8081/api/swagger-ui.html"> Swagger-UI </a>
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "YDA"))
public class OpenApiConfig {}
