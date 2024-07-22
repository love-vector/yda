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

    public OpenAiAssistantGenerator(String apiKey, String assistantId, SessionProvider sessionProvider) {
        this.threadService = new ThreadService(apiKey);
        this.assistantId = assistantId;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public SseEmitter generate(BaseAssistantRequest request) {
        String content = request.getQuery();
        String threadId = sessionProvider
                .getThreadId()
                .map(id -> {
                    threadService.addMessageToThread(id, content);
                    return id;
                })
                .orElseGet(() -> {
                    String newThreadId = threadService.createThread(content).getId();
                    sessionProvider.setThreadId(newThreadId);
                    return newThreadId;
                });

        log.debug("Thread ID: {}", threadId);
        return threadService.createRunStream(threadId, assistantId, request.getContext());
    }
}
