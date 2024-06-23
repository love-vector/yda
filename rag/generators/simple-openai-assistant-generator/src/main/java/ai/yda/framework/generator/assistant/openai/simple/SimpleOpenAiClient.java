package ai.yda.framework.generator.assistant.openai.simple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import ai.yda.framework.generator.assistant.openai.simple.util.CustomHttpHeaders;

@RequiredArgsConstructor
public class SimpleOpenAiClient {

    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void addMessageToThread(String threadId, String content) {

        var message = new HashMap<>() {
            {
                put("role", "user");
                put("content", List.of(Map.of("type", "text", "text", content)));
            }
        };

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            webClient
                    .post()
                    .uri("/threads/{thread_id}/messages", threadId)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v2")
                    .bodyValue(messageJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        System.err.println("Error status code: " + ex.getRawStatusCode());
                        System.err.println("Response body: " + ex.getResponseBodyAsString());
                    })
                    .block();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during Request serialization: ", e);
        }
    }

    public List<String> createRunStream(final String threadId, String assistantId) {

        var body = new HashMap<>() {
            {
                put("assistant_id", assistantId);
                put("stream", true);
                put("tool_choice", null);
            }
        };

        try {
            String messageJson = objectMapper.writeValueAsString(body);

            return webClient
                    .post()
                    .uri("/threads/{thread_id}/runs", threadId)
                    .header(HttpHeaders.ACCEPT, "text/event-stream")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(messageJson)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .collectList()
                    .doOnError(WebClientResponseException.class, ex -> {
                        System.err.println("Error status code: " + ex.getRawStatusCode());
                        System.err.println("Response body: " + ex.getResponseBodyAsString());
                    })
                    .block();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during Request serialization: ", e);
        }
    }
}
