package ai.yda.framework.generator.assistant.openai;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.models.*;
import lombok.RequiredArgsConstructor;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.session.SessionProvider;

@RequiredArgsConstructor
public class OpenAiAssistantGenerator implements Generator<BaseAssistantRequest, RagContext, BaseAssistantResponse> {

    private final String assistantId;

    private final AssistantsClient assistantsClient;

    private SessionProvider sessionProvider;

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request, RagContext context) {
        //        var threadRun = addMessageAndRun(request.getContent(),
        // getSessionProvider().getSession().getThreadId();
        var threadRun = addMessageAndRun(request.getContent(), null);
        var runResult = waitForResult(threadRun);
        return processResponse(runResult);
    }

    private ThreadRun addMessageAndRun(String message, String threadId) {
        if (threadId == null) {
            return assistantsClient.createThreadAndRun(new CreateAndRunThreadOptions(assistantId)
                    .setThread(new AssistantThreadCreationOptions()
                            .setMessages(List.of(new ThreadInitializationMessage(MessageRole.USER, message)))));
        }
        assistantsClient.createMessage(threadId, MessageRole.USER, message);
        return assistantsClient.createRun(threadId, new CreateRunOptions(assistantId));
    }

    private ThreadRun waitForResult(ThreadRun threadRun) {
        var atomicRun = new AtomicReference<ThreadRun>();
        try (var scheduler = Executors.newScheduledThreadPool(1)) {
            scheduler.schedule(
                    () -> {
                        var run = assistantsClient.getRun(threadRun.getThreadId(), threadRun.getId());
                        if (!run.getStatus().equals(RunStatus.IN_PROGRESS)) {
                            atomicRun.set(run);
                            scheduler.shutdown();
                        }
                    },
                    1,
                    TimeUnit.SECONDS);
            while (!scheduler.awaitTermination(500, TimeUnit.MILLISECONDS)) {}
            return atomicRun.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRunResult(ThreadRun threadRun) {
        var threadMessage = assistantsClient
                .listMessages(threadRun.getThreadId(), 1, ListSortOrder.DESCENDING, null, null)
                .getData()
                .getFirst();
        return ((MessageTextContent) threadMessage.getContent().getFirst())
                .getText()
                .getValue();
    }

    private BaseAssistantResponse processResponse(ThreadRun threadRun) {
        if (threadRun.getStatus().equals(RunStatus.FAILED)) {
            throw new RuntimeException(threadRun.getLastError().getMessage());
        }
        var result = getRunResult(threadRun);
        return BaseAssistantResponse.builder().content(result).build();
    }

    @Override
    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }
}
