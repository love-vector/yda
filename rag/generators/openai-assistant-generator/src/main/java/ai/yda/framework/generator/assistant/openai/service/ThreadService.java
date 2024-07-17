package ai.yda.framework.generator.assistant.openai.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

import ai.yda.framework.generator.assistant.openai.util.CustomHttpHeaders;

@Service
@RequiredArgsConstructor
public class ThreadService {
    private static final String BASE_URL = "https://api.openai.com/v1/threads";

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
        var message = Map.of("role", "user", "content", List.of(Map.of("type", "text", "text", content)));
        try {
            var messageJson = objectMapper.writeValueAsString(message);
            var httpEntity = new HttpEntity<>(messageJson, createHttpHeaders());
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .pathSegment(threadId, "messages")
                    .buildAndExpand()
                    .toUriString();
            restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (JsonProcessingException | HttpClientErrorException e) {
            throw new RuntimeException(e);
        }
    }

    public SseEmitter createRunStream(final String threadId, final String assistantId, final String context) {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
                    try {
                        var body = Map.of(
                                "assistant_id", assistantId,
                                "stream", true,
                                "additional_instructions", context);

                        var jsonBody = objectMapper.writeValueAsString(body);
                        var url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                                .pathSegment(threadId, "runs")
                                .buildAndExpand()
                                .toUri()
                                .toURL();
                        var httpConnection = openHttpURLConnection(url, jsonBody);

                        var reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            emitter.send(SseEmitter.event().data(line));
                        }
                        reader.close();
                        emitter.complete();
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .start();
        return emitter;
    }

    private JsonNode getThread(final String threadId) {
        try {
            var httpEntity = new HttpEntity<>(createHttpHeaders());
            var url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .pathSegment(threadId)
                    .buildAndExpand()
                    .toUriString();
            var response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException | HttpClientErrorException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode createThread() {
        try {
            var httpEntity = new HttpEntity<>(createHttpHeaders());
            var response = restTemplate.exchange(BASE_URL, HttpMethod.POST, httpEntity, String.class);
            return objectMapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpURLConnection openHttpURLConnection(URL url, String jsonBody) throws IOException {
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(HttpMethod.POST.name());
        connection.setRequestProperty(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
        connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer ".concat(apiKey));
        connection.setRequestProperty(CustomHttpHeaders.OPEN_AI_BETA, CustomHttpHeaders.OPEN_AI_VALUE);
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonBody.getBytes());
        return connection;
    }

    private HttpHeaders createHttpHeaders() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.add(CustomHttpHeaders.OPEN_AI_BETA, CustomHttpHeaders.OPEN_AI_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
