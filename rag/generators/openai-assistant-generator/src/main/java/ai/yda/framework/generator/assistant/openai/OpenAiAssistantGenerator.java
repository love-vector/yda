package ai.yda.framework.generator.assistant.openai;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.service.SessionProvider;
import ai.yda.framework.generator.assistant.openai.service.ThreadService;
import ai.yda.framework.rag.core.generator.AbstractGenerator;

@Slf4j
public class OpenAiAssistantGenerator extends AbstractGenerator<BaseAssistantRequest, SseEmitter> {
    private final ThreadService threadService;
    private final String assistantId;
    private final SessionProvider sessionProvider;

    public OpenAiAssistantGenerator(
            final String apiKey, final String assistantId, final SessionProvider sessionProvider) {
        this.threadService = new ThreadService(apiKey);
        this.assistantId = assistantId;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public SseEmitter generate(final BaseAssistantRequest request) {
        var requestQuery = request.getQuery();
        var threadId = sessionProvider
                .getThreadId()
                .map(id -> {
                    threadService.addMessageToThread(id, requestQuery);
                    return id;
                })
                .orElseGet(() -> {
                    var newThreadId = threadService.createThread(requestQuery).getId();
                    sessionProvider.setThreadId(newThreadId);
                    return newThreadId;
                });

        log.debug("Thread ID: {}", threadId);
        return threadService.createRunStream(threadId, assistantId, request.getContext());
    }
}
