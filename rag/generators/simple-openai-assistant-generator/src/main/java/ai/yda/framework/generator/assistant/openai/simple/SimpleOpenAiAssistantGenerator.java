package ai.yda.framework.generator.assistant.openai.simple;

import lombok.RequiredArgsConstructor;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.generator.assistant.openai.simple.service.ThreadService;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class SimpleOpenAiAssistantGenerator implements Generator<BaseAssistantRequest, SseEmitter> {

    private final String apiKey;
    private final String assistantId;

    @Override
    public SseEmitter generate(BaseAssistantRequest request) {

        var threadService = new ThreadService(apiKey);

        var threadId = threadService.getThreadIdForUser(null);

        threadService.addMessageToThread(String.valueOf(threadId), request.getContent() + request.getContext());

        return threadService.createRunStream(threadId, assistantId);
    }
}
