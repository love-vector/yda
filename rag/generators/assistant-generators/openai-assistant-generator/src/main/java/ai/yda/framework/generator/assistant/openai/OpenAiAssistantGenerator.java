package ai.yda.framework.generator.assistant.openai;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import ai.yda.framework.rag.base.generator.assistant.AssistantRagRequest;
import ai.yda.framework.rag.base.generator.assistant.AssistantRagResponse;
import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.models.*;

import ai.yda.framework.rag.base.generator.assistant.AssistantGenerator;

public class OpenAiAssistantGenerator extends AssistantGenerator {

    private final AssistantsClient assistantsClient;

    public OpenAiAssistantGenerator(String assistantId, AssistantsClient assistantsClient) {
        super(assistantId);
        this.assistantsClient = assistantsClient;
    }

    @Override
    public AssistantRagResponse generate(AssistantRagRequest request) {
        var threadRun = addMessageAndRun(request.getContent(), request.getThreadId());
        var runResult = waitForResult(threadRun);
        return processResponse(runResult);
    }

    protected ThreadRun addMessageAndRun(
            String message, String threadId) {
        if (threadId == null) {
            return assistantsClient.createThreadAndRun(new CreateAndRunThreadOptions(assistantId)
                    .setThread(new AssistantThreadCreationOptions().setMessages(List.of(new ThreadInitializationMessage(MessageRole.USER, message)))));
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
        return ((MessageTextContent) threadMessage.getContent().getFirst()).getText().getValue();
    }

    private AssistantRagResponse processResponse(ThreadRun threadRun) {
        if (threadRun.getStatus().equals(RunStatus.FAILED)) {
            throw new RuntimeException(threadRun.getLastError().getMessage());
        }
        var result = getRunResult(threadRun);
        return AssistantRagResponse.builder()
                .content(result)
                .build();
    }
}
