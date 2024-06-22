package ai.yda.framework.generator.assistant.openai.simple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.generator.assistant.openai.simple.util.CustomHttpHeaders;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.session.SessionProvider;

@RequiredArgsConstructor
public class SimpleOpenAiAssistantGenerator implements Generator<BaseAssistantRequest, BaseAssistantResponse> {

    private SessionProvider sessionProvider;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request) {

        addMessageToThread("thread_fIXyqifnwBEMMyrxlPfl8YqW", "who are you?");
        //        asst_9S7lP9N2n99EPept42iUjLeL
        var xx = createRunStream("thread_fIXyqifnwBEMMyrxlPfl8YqW", "asst_9S7lP9N2n99EPept42iUjLeL");

        // Extract the assistant's message content
        String assistantMessage = extractAssistantMessage(xx);
        System.out.println("Assistant Message: " + assistantMessage);

        return BaseAssistantResponse.builder().responseMessage(assistantMessage).build();
    }

    public void addMessageToThread(String threadId, String content) {
        String apiKey = "sk-proj-eTAeDDwK4dEQY09ZQktbT3BlbkFJeaKh8oDF3sZT00LtAVnf"; // Use your actual API key

        var message = new HashMap<>() {
            {
                put("role", "user");
                put("content", List.of(Map.of("type", "text", "text", content)));
            }
        };

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            webClient
                    .post()
                    .uri("/threads/{thread_id}/messages", threadId)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header("OpenAI-Beta", "assistants=v2")
                    .bodyValue(messageJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during Request serialization: ", e);
        }
    }

    public List<String> createRunStream(final String threadId, String assistantId) {
        String key =
                "sk-proj-eTAeDDwK4dEQY09ZQktbT3BlbkFJeaKh8oDF3sZT00LtAVnf"; // Blocking to get the key synchronously

        var body = new HashMap<>() {
            {
                put("assistant_id", assistantId);
                put("stream", true);
                put("tool_choice", null);
            }
        };

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String messageJson = objectMapper.writeValueAsString(body);

            return webClient
                    .post()
                    .uri("/threads/{thread_id}/runs", threadId)
                    .header(HttpHeaders.ACCEPT, "text/event-stream")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + key)
                    .header(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v1")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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

    private String extractAssistantMessage(List<String> response) {
        for (String json : response) {
            try {
                JsonNode node = new ObjectMapper().readTree(json);
                if (node.has("content")) {
                    JsonNode contentNode = node.get("content");
                    for (JsonNode content : contentNode) {
                        if (content.has("text") && content.get("text").has("value")) {
                            return content.get("text").get("value").asText();
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error during Assistant's response processing", e);
            }
        }
        return "";
    }

    @Override
    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }
}
