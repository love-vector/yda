package ai.yda.llm.openai;

import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class OpenAiConfig {

    public static final String OPENAI_SERVICE_BEAN_NAME = "openAiService";

    public static final String OPENAI_WEB_CLIENT_BEAN_NAME = "openAiWebClient";

    private final OpenAiConnectionProperties openAiConnectionProperties;

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean(OPENAI_SERVICE_BEAN_NAME)
    @Order
    public OpenAiService openAiService() {
        return new OpenAiService(openAiConnectionProperties.getApiKey());
    }

    @Bean(OPENAI_WEB_CLIENT_BEAN_NAME)
    @Order
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiConnectionProperties.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("OpenAI-Beta", "assistants=v2")
                .build();
    }
}
