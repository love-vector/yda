package org.vector.assistant.client;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.vector.assistant.dto.RunRequest;
import org.vector.assistant.util.constant.CustomHttpHeaders;

@Service
@RequiredArgsConstructor
public class RunClient {

    @Value("${spring.ai.openai.api-key}")
    private String token;

    private final WebClient webClient;

    /**
     * Initiates a new run in the specified thread by sending a POST request to the OpenAI API.
     *
     * @param threadId   The unique identifier of the thread where the run is to be created.
     * @param runRequest The {@link RunRequest} object that contains the details of the run to be
     *                   created.
     * @return A {@link Flux <String>} that emits the server's response as a stream of events (SSE).
     */
    public Flux<String> createRunStream(final String threadId, final RunRequest runRequest) {
        return webClient
                .post()
                .uri("/v1/threads/{thread_id}/runs", threadId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v1")
                .bodyValue(runRequest)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
