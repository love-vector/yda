package ai.yda.framework.generator.assistant.openai;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class OpenAiAssistantGenerator implements Generator<BaseAssistantRequest, AssistantMessage> {

    private final OpenAiChatModel chatModel;

    @Override
    public AssistantMessage generate(BaseAssistantRequest request) {

        Prompt prompt = new Prompt(new UserMessage(request.getContent()));

        return chatModel.call(prompt).getResult().getOutput();
    }
}
