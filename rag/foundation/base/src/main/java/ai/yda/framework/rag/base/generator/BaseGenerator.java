package ai.yda.framework.rag.base.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class BaseGenerator implements Generator<BaseAssistantRequest, AssistantMessage> {

    private final ChatModel chat;

    @Override
    public AssistantMessage generate(final BaseAssistantRequest request) {
        var response = chat.call(new Prompt(request.getQuery()));
        return response.getResult().getOutput();
    }
}
