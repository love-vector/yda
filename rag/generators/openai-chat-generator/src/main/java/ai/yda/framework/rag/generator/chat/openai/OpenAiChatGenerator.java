package ai.yda.framework.rag.generator.chat.openai;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

@RequiredArgsConstructor
public class OpenAiChatGenerator implements Generator<RagRequest, RagResponse> {

    private final OpenAiChatModel chatModel;

    @Override
    public RagResponse generate(final RagRequest request, final String context) {
        var prompt = new Prompt(new UserMessage(request.getQuery()));
        var response = chatModel.call(prompt).getResult().getOutput();
        return RagResponse.builder().result(response.getContent()).build();
    }
}
