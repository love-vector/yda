package ai.yda.framework.generator.chat.openai;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class OpenAiChatGenerator implements Generator<BaseAssistantRequest, AssistantMessage> {

    private final OpenAiChatModel chatModel;

    @Override
    public AssistantMessage generate(BaseAssistantRequest request) {

        Prompt prompt = new Prompt(new UserMessage(request.getQuery()));

        return chatModel.call(prompt).getResult().getOutput();
    }
}
