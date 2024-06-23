package ai.yda.framework.generator.assistant.openai.simple;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

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

        var threadService = new ThreadService(apiKey);

        var threadId = threadService.getThreadIdForUser(null);

        threadService.addMessageToThread(
                String.valueOf(threadId), request.getContent() + request.getContext());

        var response = threadService.createRunStream(threadId, assistantId);

        // Extract the assistant's message content
        var assistantMessage = extractAssistantMessage(response);

        return BaseAssistantResponse.builder().responseMessage(assistantMessage).build();
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
}
