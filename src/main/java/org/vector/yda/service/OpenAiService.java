package org.vector.yda.service;

import java.util.List;

import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.assistants.ModifyAssistantRequest;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.ai.openai.api.common.OpenAiApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.vector.yda.config.OpenAiConfig;
import org.vector.yda.model.dto.AssistantDto;
import org.vector.yda.model.dto.OpenAiRunDto;

@Service("YDA_OPEN_AI_SERVICE")
@RequiredArgsConstructor
public class OpenAiService {

    @Qualifier(OpenAiConfig.OPENAI_SERVICE_BEAN_NAME)
    private final com.theokanning.openai.service.OpenAiService openAiService;

    @Qualifier(OpenAiConfig.OPENAI_WEB_CLIENT_BEAN_NAME)
    private final WebClient openAiWebClient;

    public Assistant createAssistant(final AssistantDto assistantDto) {
        return openAiService.createAssistant(AssistantRequest.builder()
                .name(assistantDto.name())
                .instructions(assistantDto.instructions())
                .model("gpt-4-1106-preview")
                .build());
    }

    public void updateAssistant(final String assistantId, final ModifyAssistantRequest request) {
        openAiService.modifyAssistant(assistantId, request);
    }

    public void deleteAssistant(final String assistantId) {
        var result = openAiService.deleteAssistant(assistantId);
        if (!result.isDeleted()) {
            throw new OpenAiApiException("Failed to delete assistant - " + assistantId);
        }
    }

    public Thread createThread() {
        return openAiService.createThread(
                ThreadRequest.builder().messages(List.of()).build());
    }

    public Message addMessageToThread(final String threadId, final String message) {
        return openAiService.createMessage(
                threadId,
                MessageRequest.builder()
                        .role(ChatMessageRole.USER.value())
                        .content(message)
                        .build());
    }

    public Flux<String> streamRun(final String assistantId, final String threadId) {
        return openAiWebClient
                .post()
                .uri("/v1/threads/{thread_id}/runs", threadId)
                .bodyValue(OpenAiRunDto.builder().assistantId(assistantId).build())
                .retrieve()
                .bodyToFlux(String.class);
    }
}
