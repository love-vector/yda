package ai.yda.framework.generator.assistant.openai;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class OpenAiAssistantGenerator implements Generator<BaseAssistantRequest, BaseAssistantResponse> {

    private final OpenAiChatModel chatModel;

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request) {

        Prompt prompt = new Prompt(new UserMessage(request.getContent()));
        ChatResponse call = chatModel.call(prompt);
        return BaseAssistantResponse.builder()
                .responseMessage(Map.of("generation", call).toString())
                .build();
    }
}
