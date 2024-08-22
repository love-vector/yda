/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.rag.generator.shared;

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
import com.azure.ai.openai.assistants.models.MessageDeltaChunk;
import com.azure.ai.openai.assistants.models.MessageDeltaTextContentObject;
import com.azure.ai.openai.assistants.models.MessageRole;
import com.azure.ai.openai.assistants.models.MessageTextContent;
import com.azure.ai.openai.assistants.models.RunStatus;
import com.azure.ai.openai.assistants.models.StreamMessageUpdate;
import com.azure.ai.openai.assistants.models.ThreadMessageOptions;
import com.azure.ai.openai.assistants.models.ThreadRun;
import com.azure.core.credential.KeyCredential;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class AzureOpenAiAssistantService {

    private final AssistantsClient assistantsClient;

    public AzureOpenAiAssistantService(final String apiKey) {
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
        try {
            var executor = Executors.newSingleThreadScheduledExecutor();
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
            executor.shutdown();
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

    public Flux<String> createRunStream(final String threadId, final String assistantId, final String context) {
        var createRunOptions = new CreateRunOptions(assistantId).setAdditionalInstructions(context);
        return Flux.fromIterable(assistantsClient.createRunStream(threadId, createRunOptions))
                .subscribeOn(Schedulers.boundedElastic())
                .filter(streamUpdate -> streamUpdate instanceof StreamMessageUpdate)
                .map(deltaMessage -> extractDeltaContent(((StreamMessageUpdate) deltaMessage).getMessage()));
    }

    private String getLastMessage(final String threadId) {
        var messages = assistantsClient.listMessages(threadId);
        return messages.getData().get(0).getContent().stream()
                .map(content -> ((MessageTextContent) content).getText().getValue())
                .collect(Collectors.joining(". "));
    }

    private String extractDeltaContent(final MessageDeltaChunk delta) {
        return delta.getDelta().getContent().parallelStream()
                .map(content ->
                        ((MessageDeltaTextContentObject) content).getText().getValue())
                .collect(Collectors.joining(". "));
    }
}
