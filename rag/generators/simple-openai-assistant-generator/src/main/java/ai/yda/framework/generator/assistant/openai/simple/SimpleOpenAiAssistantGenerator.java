package ai.yda.framework.generator.assistant.openai.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.generator.assistant.openai.simple.service.ThreadService;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class SimpleOpenAiAssistantGenerator implements Generator<BaseAssistantRequest, BaseAssistantResponse> {

    private final String apiKey;
    private final String assistantId;

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request) {
        throw new RuntimeException("not available");
    }

    @Override
    public Flux<BaseAssistantResponse> generateReactive(BaseAssistantRequest request) {
        ThreadService threadService = new ThreadService(apiKey);

        return threadService
                .getThreadIdForUser(null)
                .flatMapMany(threadId -> threadService
                        .addMessageToThread(threadId, request.getContent() + request.getContext())
                        .thenMany(threadService.createRunStream(threadId, assistantId)))
                .map(this::extractAssistantMessage)
                .map(message ->
                        BaseAssistantResponse.builder().responseMessage(message).build());
    }

    private String extractAssistantMessage(String json) {
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
        return "";
    }
}
