package ai.yda.application.config;

import java.util.List;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.assistant.RagAssistant;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.generator.assistant.openai.OpenAiAssistantGenerator;
import ai.yda.framework.rag.base.application.BaseRagApplication;
import ai.yda.framework.rag.base.augmenter.BaseAugmenter;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

@Configuration
@RequiredArgsConstructor
public class AssistantConfiguration {
    private final List<Retriever<BaseAssistantRequest, BaseRagContext>> retrievers;
    private final OpenAiAssistantGenerator openAiAssistantGenerator;
    private final List<Channel<BaseAssistantRequest, SseEmitter>> channels;

    @PostConstruct
    public void init() {
        var websiteRagApplication = new BaseRagApplication<>(retrievers, new BaseAugmenter(), openAiAssistantGenerator);
        new RagAssistant<>(websiteRagApplication, channels);
    }
}
