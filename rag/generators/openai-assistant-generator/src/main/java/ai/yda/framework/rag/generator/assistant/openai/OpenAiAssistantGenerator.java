package ai.yda.framework.rag.generator.assistant.openai;

import lombok.extern.slf4j.Slf4j;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.generator.assistant.openai.service.ThreadService;
import ai.yda.framework.session.core.SessionProvider;

@Slf4j
public class OpenAiAssistantGenerator implements Generator<RagRequest, RagResponse> {

    private static final String THREAD_ID_KEY = "threadId";

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
    public RagResponse generate(final RagRequest request, final String context) {
        var requestQuery = request.getQuery();
        var threadId = sessionProvider
                .get(THREAD_ID_KEY)
                .map(Object::toString)
                .map(id -> {
                    threadService.addMessageToThread(id, requestQuery);
                    return id;
                })
                .orElseGet(() -> {
                    var newThreadId = threadService.createThread(requestQuery).getId();
                    sessionProvider.put(THREAD_ID_KEY, newThreadId);
                    return newThreadId;
                });
        log.debug("Thread ID: {}", threadId);
        return RagResponse.builder()
                .result(threadService.createRunAndWaitForResponse(threadId, assistantId, context))
                .build();
    }
}
