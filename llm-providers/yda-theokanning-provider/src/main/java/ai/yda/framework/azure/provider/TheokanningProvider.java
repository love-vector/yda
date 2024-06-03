package ai.yda.framework.azure.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import com.google.gson.Gson;
import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.assistants.ModifyAssistantRequest;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.generator.llm.Assistant;
import ai.yda.framework.rag.generator.llm.LlmProvider;
import ai.yda.framework.rag.generator.llm.Message;
import ai.yda.framework.rag.generator.llm.Thread;

@RequiredArgsConstructor
public class TheokanningProvider implements LlmProvider {

    private final String model;

    private final OpenAiService openAiService;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public Assistant getAssistant(final String assistantId) {
        return TheokanningMapper.INSTANCE.toAssistant(openAiService.retrieveAssistant(assistantId));
    }

    @Override
    public List<Assistant> getAssistants() {
        return openAiService.listAssistants(ListSearchParameters.builder().build()).getData().stream()
                .map(TheokanningMapper.INSTANCE::toAssistant)
                .toList();
    }

    @Override
    public Assistant createAssistant(final Assistant assistant) {
        return TheokanningMapper.INSTANCE.toAssistant(openAiService.createAssistant(AssistantRequest.builder()
                .model(model)
                .name(assistant.getName())
                .instructions(assistant.getInstructions())
                .build()));
    }

    @Override
    public void updateAssistant(final String assistantId, final Assistant assistant) {
        openAiService.modifyAssistant(
                assistantId,
                ModifyAssistantRequest.builder()
                        .model(model)
                        .name(assistant.getName())
                        .instructions(assistant.getInstructions())
                        .build());
    }

    @Override
    public void deleteAssistant(final String assistantId) {
        openAiService.deleteAssistant(assistantId);
    }

    @Override
    public Thread createThread(final Thread thread) {
        return TheokanningMapper.INSTANCE.toThread(
                openAiService.createThread(ThreadRequest.builder().build()));
    }

    @Override
    public Message createMessage(final String threadId, final Message message) {
        return TheokanningMapper.INSTANCE.toMessage(openAiService.createMessage(
                threadId, MessageRequest.builder().content(message.getContent()).build()));
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
