package org.vector.assistant.config;

import java.time.Duration;

import com.theokanning.openai.service.OpenAiService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openAiApiKey, Duration.ofMinutes(1));
    }
}
