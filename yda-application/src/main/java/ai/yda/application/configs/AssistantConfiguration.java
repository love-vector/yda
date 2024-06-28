package ai.yda.application.configs;

import java.util.List;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;

import ai.yda.application.channels.AsyncChannel;
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
        var ragApplication =
                new BaseRagApplication<>(websiteRetriever, new BaseChainAugmenter(), openAiAssistantGenerator);
        new RagAssistant<>(ragApplication, List.of(asyncController));
    }
}
