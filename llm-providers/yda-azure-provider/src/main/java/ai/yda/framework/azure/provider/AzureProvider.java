package ai.yda.framework.azure.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.models.AssistantCreationOptions;
import com.azure.ai.openai.assistants.models.AssistantThreadCreationOptions;
import com.azure.ai.openai.assistants.models.MessageRole;
import com.azure.ai.openai.assistants.models.UpdateAssistantOptions;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.generator.Assistant;
import ai.yda.framework.rag.generator.LlmProvider;
import ai.yda.framework.rag.generator.Message;
import ai.yda.framework.rag.generator.Thread;

/**/

@RequiredArgsConstructor
public class AzureProvider implements LlmProvider {

    private final String model;

    private final AssistantsClient assistantsClient;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public Assistant getAssistant(final String assistantId) {
        return AzureMapper.INSTANCE.toAssistant(assistantsClient.getAssistant(assistantId));
    }

    @Override
    public List<Assistant> getAssistants() {
        return assistantsClient.listAssistants().getData().stream()
                .map(AzureMapper.INSTANCE::toAssistant)
                .toList();
    }

    @Override
    public Assistant createAssistant(final Assistant assistant) {
        return AzureMapper.INSTANCE.toAssistant(assistantsClient.createAssistant(new AssistantCreationOptions(model)
                .setName(assistant.getName())
                .setInstructions(assistant.getInstructions())));
    }

    @Override
    public void updateAssistant(final String assistantId, final Assistant assistant) {
        assistantsClient.updateAssistant(
                assistantId,
                new UpdateAssistantOptions().setName(assistant.getName()).setInstructions(assistant.getInstructions()));
    }

    @Override
    public void deleteAssistant(final String assistantId) {
        assistantsClient.deleteAssistant(assistantId);
    }

    @Override
    public Thread createThread(final Thread thread) {
        return AzureMapper.INSTANCE.toThread(assistantsClient.createThread(new AssistantThreadCreationOptions()));
    }

    @Override
    public Message createMessage(final String threadId, final Message message) {
        return AzureMapper.INSTANCE.toMessage(
                assistantsClient.createMessage(threadId, MessageRole.USER, message.getContent()));
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
