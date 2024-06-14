package ai.yda.framework.rag.base.generator;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;

import ai.yda.framework.rag.base.model.BaseRagRequest;
import ai.yda.framework.rag.base.model.BaseRagResponse;
import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public class BaseGenerator implements Generator<BaseRagRequest, BaseRagResponse> {

    private final ChatModel chat;

    @Override
    public BaseRagResponse generate(BaseRagRequest request) {
        var response = chat.call(new Prompt(request.getContent()));
        return BaseRagResponse.builder()
                .content(response.getResult().getOutput().getContent())
                .build();
    }
}
