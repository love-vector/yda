package ai.yda.framework.azure.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.models.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.llm.AssistantPrototype;
import ai.yda.framework.llm.LlmProvider;
import ai.yda.framework.llm.MessagePrototype;
import ai.yda.framework.llm.ThreadPrototype;

@RequiredArgsConstructor
public class AzureProvider implements LlmProvider {

    private final String model;

    private final AssistantsClient assistantsClient;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public AssistantPrototype getAssistant(final String assistantId) {
        return Assistant.builder()
                .assistant(assistantsClient.getAssistant(assistantId))
                .build();
    }

    @Override
    public List<AssistantPrototype> getAssistants() {
        return assistantsClient.listAssistants().getData().stream()
                .map(assistant -> (AssistantPrototype)
                        Assistant.builder().assistant(assistant).build())
                .toList();
    }

    @Override
    public AssistantPrototype createAssistant(final AssistantPrototype assistantPrototype) {
        var assistant = assistantsClient.createAssistant(new AssistantCreationOptions(model)
                .setName(assistantPrototype.getName())
                .setInstructions(assistantPrototype.getInstructions()));
        return Assistant.builder().assistant(assistant).build();
    }

    @Override
    public void updateAssistant(final String assistantId, final AssistantPrototype assistantPrototype) {
        assistantsClient.updateAssistant(
                assistantId,
                new UpdateAssistantOptions()
                        .setName(assistantPrototype.getName())
                        .setInstructions(assistantPrototype.getInstructions()));
    }

    @Override
    public void deleteAssistant(final String assistantId) {
        assistantsClient.deleteAssistant(assistantId);
    }

    @Override
    public ThreadPrototype createThread(final ThreadPrototype threadPrototype) {
        var assistantThread = assistantsClient.createThread(new AssistantThreadCreationOptions());
        return Thread.builder().thread(assistantThread).build();
    }

    @Override
    public MessagePrototype createMessage(final String threadId, final MessagePrototype messagePrototype) {
        var message = assistantsClient.createMessage(threadId, MessageRole.USER, messagePrototype.getContent());
        return Message.builder().message(message).build();
    }

    @Override
    public Flow.Publisher<String> streamRun(final String assistantId, final String threadId) {
        var dto = OpenAiRunDto.builder().assistantId(assistantId).build();
        var publisher = new SubmissionPublisher<String>();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("/v1/threads/%s/runs", threadId)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(dto)))
                .build();
        httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .whenComplete((body, ex) -> {
                    if (ex != null) {
                        publisher.closeExceptionally(ex);
                    } else {
                        publisher.submit(body);
                        publisher.close();
                    }
                });
        return publisher;
    }
}
