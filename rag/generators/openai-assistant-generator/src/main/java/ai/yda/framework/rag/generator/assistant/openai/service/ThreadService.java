package ai.yda.framework.rag.generator.assistant.openai.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.ai.openai.assistants.models.AssistantThread;
import com.azure.ai.openai.assistants.models.AssistantThreadCreationOptions;
import com.azure.ai.openai.assistants.models.CreateRunOptions;
import com.azure.ai.openai.assistants.models.MessageRole;
import com.azure.ai.openai.assistants.models.MessageTextContent;
import com.azure.ai.openai.assistants.models.RunStatus;
import com.azure.ai.openai.assistants.models.StreamMessageUpdate;
import com.azure.ai.openai.assistants.models.ThreadMessageOptions;
import com.azure.ai.openai.assistants.models.ThreadRun;
import com.azure.core.credential.KeyCredential;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class ThreadService {
    private final AssistantsClient assistantsClient;

    public ThreadService(final String apiKey) {
        this.assistantsClient = new AssistantsClientBuilder()
                .credential(new KeyCredential(apiKey))
                .buildClient();
    }

    public AssistantThread createThread(final String content) {
        var threadMessageOptions = new ThreadMessageOptions(MessageRole.USER, content);
        var creationOptions = new AssistantThreadCreationOptions().setMessages(List.of(threadMessageOptions));
        return assistantsClient.createThread(creationOptions);
    }

    public void addMessageToThread(final String threadId, final String content) {
        var threadMessageOptions = new ThreadMessageOptions(MessageRole.USER, content);
        assistantsClient.createMessage(threadId, threadMessageOptions);
    }

    public String createRunAndWaitForResponse(final String threadId, final String assistantId, final String context) {
        var createRunOptions = new CreateRunOptions(assistantId).setAdditionalInstructions(context);
        var threadRun = assistantsClient.createRun(threadId, createRunOptions);
        threadRun = waitForRunToFinish(threadRun);
        return getLastMessage(threadRun.getThreadId());
    }

    private ThreadRun waitForRunToFinish(final ThreadRun threadRun) {
        var atomicThreadRun = new AtomicReference<>(threadRun);
        try (var executor = Executors.newSingleThreadScheduledExecutor()) {
            var schedule = executor.scheduleAtFixedRate(
                    () -> {
                        var progressThreadRun = assistantsClient.getRun(threadRun.getThreadId(), threadRun.getId());
                        var progressStatus = progressThreadRun.getStatus();
                        if (progressStatus != RunStatus.QUEUED && progressStatus != RunStatus.IN_PROGRESS) {
                            atomicThreadRun.set(progressThreadRun);
                            executor.shutdown();
                        }
                    },
                    1,
                    1,
                    TimeUnit.SECONDS);
            schedule.get();
        } catch (ExecutionException | InterruptedException exception) {
            throw new RuntimeException(
                    String.format(
                            "Error while waiting for thread run: threadId - %s, runId - %s",
                            threadRun.getThreadId(), threadRun.getId()),
                    exception);
        } catch (CancellationException ignored) {
        }
        return atomicThreadRun.get();
    }

    private String getLastMessage(final String threadId) {
        var messages = assistantsClient.listMessages(threadId);
        return messages.getData().getFirst().getContent().stream()
                .map(content -> ((MessageTextContent) content).getText().getValue())
                .collect(Collectors.joining(". "));
    }

    public SseEmitter createRunStream(final String threadId, final String assistantId, final String context) {
        var emitter = new SseEmitter();
        var createRunOptions = new CreateRunOptions(assistantId).setAdditionalInstructions(context);
        assistantsClient.createRunStream(threadId, createRunOptions).stream().forEach(streamUpdate -> {
            try {
                if (streamUpdate instanceof StreamMessageUpdate deltaMessage) {
                    emitter.send(deltaMessage.getMessage());
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
        emitter.complete();
        return emitter;
    }
}
