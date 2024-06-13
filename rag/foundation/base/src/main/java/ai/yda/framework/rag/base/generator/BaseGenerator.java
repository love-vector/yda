package ai.yda.framework.rag.base.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.rag.base.model.BaseRagContext;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class BaseGenerator implements Generator<BaseAssistantRequest, BaseRagContext, BaseAssistantResponse> {

    private final ChatModel chat;

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request, BaseRagContext context) {
        var response = chat.call(new Prompt(request.getContent()));
        return BaseAssistantResponse.builder()
                .content(response.getResult().getOutput().getContent())
                .build();
    }
}
