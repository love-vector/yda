package ai.yda.framework.generator.assistant.openai.simple.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import ai.yda.framework.generator.assistant.openai.simple.util.CustomHttpHeaders;

@RequiredArgsConstructor
public class ThreadService {

    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public Mono<Void> addMessageToThread(String threadId, String content) {
        var message = new HashMap<>() {
            {
                put("role", "user");
                put("content", List.of(Map.of("type", "text", "text", content)));
            }
        };

        try {
            var messageJson = objectMapper.writeValueAsString(message);
            return webClient
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
                    .then();
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error during Request serialization: ", e));
        }
    }

    public Flux<String> createRunStream(String threadId, String assistantId) {
        var body = new HashMap<>() {
            {
                put("assistant_id", assistantId);
                put("stream", true);
                put("tool_choice", null);
            }
        };

        try {
            var messageJson = objectMapper.writeValueAsString(body);

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
                    .doOnError(WebClientResponseException.class, ex -> {
                        System.err.println("Error status code: " + ex.getRawStatusCode());
                        System.err.println("Response body: " + ex.getResponseBodyAsString());
                    });
        } catch (JsonProcessingException e) {
            return Flux.error(new RuntimeException("Error during Request serialization: ", e));
        }
    }

    public Mono<String> getThreadIdForUser(String sessionId) {
        String threadId = null; // Replace with your logic to get threadId

        if (threadId == null) {
            return createThread(generateSessionId()).map(node -> node.get("id").asText());
        }

        return getThread(threadId).map(node -> node.get("id").asText());
    }

    private Mono<JsonNode> getThread(String threadId) {
        return webClient
                .get()
                .uri("/threads/{thread_id}", threadId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header("OpenAI-Beta", "assistants=v2")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        return objectMapper.readTree(response);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error processing JSON response", e);
                    }
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.empty());
    }

    private Mono<JsonNode> createThread(String sessionId) {
        var body = new HashMap<>() {
            {
                put("metadata", Map.of("session_id", sessionId));
            }
        };

        try {
            var requestBody = objectMapper.writeValueAsString(body);
            return webClient
                    .post()
                    .uri("/threads")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header("OpenAI-Beta", "assistants=v2")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> {
                        try {
                            return objectMapper.readTree(response);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error during request serialization: ", e);
                        }
                    });
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error during request serialization: ", e));
        }
    }

    private static String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
