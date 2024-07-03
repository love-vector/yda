package ai.yda.application.config;

import java.util.List;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;

import ai.yda.application.channel.AsyncChannel;
import ai.yda.framework.core.assistant.RagAssistant;
import ai.yda.framework.generator.assistant.openai.simple.SimpleOpenAiAssistantGenerator;
import ai.yda.framework.rag.base.application.BaseRagApplication;
import ai.yda.framework.rag.base.augmenter.BaseChainAugmenter;
import ai.yda.framework.rag.retriever.website.WebsiteRetriever;

@Configuration
@RequiredArgsConstructor
public class AssistantConfiguration {

    private final WebsiteRetriever websiteRetriever;
    private final SimpleOpenAiAssistantGenerator openAiAssistantGenerator;
    private final AsyncChannel asyncController;

    @PostConstruct
    public void init() {
        var websiteRagApplication =
                new BaseRagApplication<>(websiteRetriever, new BaseChainAugmenter(), openAiAssistantGenerator);
        new RagAssistant<>(websiteRagApplication, List.of(asyncController));
    }
}
