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

/**
 * Provides methods to interact with the Azure OpenAI Assistant API. It facilitates creating and managing conversation
 * Threads, sending messages, and retrieving Responses from the Assistant service.
 * <p>
 * This service uses an {@link AssistantsClient} to perform operations such as creating Threads, adding messages,
 * and running Assistant tasks. It also supports streaming Responses for real-time updates.
 * </p>
 *
 * @author Nikita Litvinov
 * @author Iryna Kopchak
 * @see AssistantsClient
 * @see AssistantThread
 * @since 0.1.0
 */
public class AzureOpenAiAssistantService {

    private final AssistantsClient assistantsClient;

    /**
     * Constructs a new {@link AzureOpenAiAssistantService} instance with the specified API key.
     * Initializes the {@link AssistantsClient} with the provided API key for authentication.
     *
     * @param apiKey the API key for authenticating to the OpenAI Assistant API.
     */
    public AzureOpenAiAssistantService(final String apiKey) {
        this.assistantsClient = new AssistantsClientBuilder()
                .credential(new KeyCredential(apiKey))
                .buildClient();
    }

    /**
     * Creates a new Thread in the Azure OpenAI Assistant Service with an initial message.
     *
     * @param content the content of the initial message to include in the Thread.
     * @return the {@link AssistantThread} representing the created Thread.
     */
    public AssistantThread createThread(final String content) {
        var threadMessageOptions = new ThreadMessageOptions(MessageRole.USER, content);
        var creationOptions = new AssistantThreadCreationOptions().setMessages(List.of(threadMessageOptions));
        return assistantsClient.createThread(creationOptions);
    }

    /**
     * Adds a message to an existing Thread in the Azure OpenAI Assistant Service.
     *
     * @param threadId the ID of the Thread to which the message should be added.
     * @param content  the content of the message to add.
     */
    public void addMessageToThread(final String threadId, final String content) {
        var threadMessageOptions = new ThreadMessageOptions(MessageRole.USER, content);
        assistantsClient.createMessage(threadId, threadMessageOptions);
    }

    /**
     * Creates a run for a given Thread and waits for the Response.
     *
     * @param threadId    the ID of the Thread in which to create the run.
     * @param assistantId the ID of the Assistant to use for the run.
     * @param context     additional instructions to include in the run.
     * @return the content of the last message in the Thread after the run completes.
     */
    public String createRunAndWaitForResponse(final String threadId, final String assistantId, final String context) {
        var createRunOptions = new CreateRunOptions(assistantId).setAdditionalInstructions(context);
        var threadRun = assistantsClient.createRun(threadId, createRunOptions);
        threadRun = waitForRunToFinish(threadRun);
        return getLastMessage(threadRun.getThreadId());
    }

    /**
     * Streams responses from the Azure OpenAI Assistant Service for a given run.
     *
     * @param threadId    the ID of the Thread for which to stream Responses.
     * @param assistantId the ID of the Assistant to use for the run.
     * @param context     additional instructions to include in the run.
     * @return a {@link Flux stream} of Response content updates as they are received.
     */
    public Flux<String> createRunStream(final String threadId, final String assistantId, final String context) {
        var createRunOptions = new CreateRunOptions(assistantId).setAdditionalInstructions(context);
        return Flux.fromIterable(assistantsClient.createRunStream(threadId, createRunOptions))
                .subscribeOn(Schedulers.boundedElastic())
                .filter(streamUpdate -> streamUpdate instanceof StreamMessageUpdate)
                .map(deltaMessage -> extractDeltaContent(((StreamMessageUpdate) deltaMessage).getMessage()));
    }

    /**
     * Waits for the completion of a given Thread run. It periodically polls the status of the Thread run until it is
     * no longer in the QUEUED or IN_PROGRESS states. The Thread run is updated with the final
     * {@code progressThreadRun} once the polling completes.
     *
     * @param threadRun the initial {@link ThreadRun} object representing the run to be monitored.
     * @return the updated {@link ThreadRun} object once the run has completed.
     * @throws RuntimeException if an error occurs while waiting for the Thread run to finish.
     */
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
            executor.close();
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

    /**
     * Queries all messages in the specified Thread and returns the content of the most recent message.
     *
     * @param threadId the ID of the Thread from which to retrieve the last message.
     * @return the content of the last message in the Thread.
     */
    private String getLastMessage(final String threadId) {
        var messages = assistantsClient.listMessages(threadId);
        return messages.getData().get(0).getContent().stream()
                .map(content -> ((MessageTextContent) content).getText().getValue())
                .collect(Collectors.joining(". "));
    }

    /**
     * Processes a {@link MessageDeltaChunk} to extract the text content updates and concatenates them into a single
     * string.
     *
     * @param delta the {@link MessageDeltaChunk} containing delta content updates.
     * @return the concatenated delta content as a string.
     */
    private String extractDeltaContent(final MessageDeltaChunk delta) {
        return delta.getDelta().getContent().parallelStream()
                .map(content ->
                        ((MessageDeltaTextContentObject) content).getText().getValue())
                .collect(Collectors.joining(". "));
    }
}
