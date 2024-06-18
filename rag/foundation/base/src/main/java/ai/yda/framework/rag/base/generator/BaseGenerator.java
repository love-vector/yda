package ai.yda.framework.rag.base.generator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.session.SessionProvider;

@RequiredArgsConstructor
public class BaseGenerator implements Generator<BaseAssistantRequest, BaseAssistantResponse> {

    private final ChatModel chat;

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request) {
        var response = chat.call(new Prompt(request.getContent()));
        return BaseAssistantResponse.builder()
                .content(response.getResult().getOutput().getContent())
                .build();
    }

    @Override
    public SessionProvider getSessionProvider() {
        throw new NotImplementedException("Session is not available for this Generator");
    }
}
