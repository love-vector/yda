package ai.yda.framework.generator.assistant.openai.simple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.service.SessionProvider;
import ai.yda.framework.generator.assistant.openai.simple.service.ThreadService;
import ai.yda.framework.rag.core.generator.AbstractGenerator;

@RequiredArgsConstructor
@Slf4j
public class SimpleOpenAiAssistantGenerator extends AbstractGenerator<BaseAssistantRequest, SseEmitter> {

    private final String apiKey;
    private final String assistantId;
    private final SessionProvider sessionProvider;

    @Override
    public SseEmitter generate(BaseAssistantRequest request) {
        var threadService = new ThreadService(apiKey);

        var threadId = threadService
                .getOrCreateThread(sessionProvider.getThreadId())
                .get("id")
                .textValue();

        updateSessionThreadId(threadId);

        threadService.addMessageToThread(threadId, request.getQuery());
        return threadService.createRunStream(threadId, assistantId, request.getContext());
    }

    private void updateSessionThreadId(final String threadId) {
        sessionProvider.setThreadId(threadId);
        log.info("Thread ID: {}", threadId);
    }
}
