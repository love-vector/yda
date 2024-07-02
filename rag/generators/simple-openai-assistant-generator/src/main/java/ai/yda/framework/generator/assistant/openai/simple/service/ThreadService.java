package ai.yda.framework.generator.assistant.openai.simple.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.yda.framework.generator.assistant.openai.simple.util.CustomHttpHeaders;

@Service
@RequiredArgsConstructor
public class ThreadService {

    private final String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public JsonNode getOrCreateThread(final String threadId) {

        if (threadId == null) {
            return createThread();
        }
        return getThread(threadId);
    }

    public void addMessageToThread(final String threadId, final String content) {
        var message = new HashMap<String, Object>() {
            {
                put("role", "user");
                put("content", List.of(Map.of("type", "text", "text", content)));
            }
        };

        try {
            var messageJson = objectMapper.writeValueAsString(message);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.add(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v2");

            HttpEntity<String> entity = new HttpEntity<>(messageJson, headers);
            restTemplate.exchange(
                    "https://api.openai.com/v1/threads/{thread_id}/messages",
                    HttpMethod.POST,
                    entity,
                    String.class,
                    threadId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during Request serialization: ", e);
        } catch (HttpClientErrorException ex) {
            System.err.println("Error status code: " + ex.getStatusCode());
            System.err.println("Response body: " + ex.getResponseBodyAsString());
        }
    }

    public SseEmitter createRunStream(final String threadId, final String assistantId) {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
                    try {
                        var body = new HashMap<String, Object>() {
                            {
                                put("assistant_id", assistantId);
                                put("stream", true);
                                put("tool_choice", null);
                            }
                        };

                        var bodyJson = objectMapper.writeValueAsString(body);
                        URL url = new URL("https://api.openai.com/v1/threads/" + threadId + "/runs");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty(HttpHeaders.ACCEPT, "text/event-stream");
                        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
                        connection.setRequestProperty(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v1");
                        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        connection.setDoOutput(true);
                        connection.getOutputStream().write(bodyJson.getBytes());

                        try (BufferedReader reader =
                                new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                emitter.send(SseEmitter.event().data(line));
                            }
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        } finally {
                            emitter.complete();
                        }
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .start();

        return emitter;
    }

    private JsonNode getThread(final String threadId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.add(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v2");

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.openai.com/v1/threads/{thread_id}", HttpMethod.GET, entity, String.class, threadId);

            return objectMapper.readTree(response.getBody());
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response", e);
        }
    }

    private JsonNode createThread() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.add(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v2");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response =
                    restTemplate.exchange("https://api.openai.com/v1/threads", HttpMethod.POST, entity, String.class);

            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during request serialization: ", e);
        }
    }
}
