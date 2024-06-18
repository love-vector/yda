package ai.yda.application;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.assistant.RagAssistant;
import ai.yda.framework.core.channel.factory.netty.HttpNettyChannelFactory;
import ai.yda.framework.rag.base.application.BaseRagApplication;
import ai.yda.framework.rag.base.augmenter.BaseAugmenter;
import ai.yda.framework.rag.base.retriever.BaseRetriever;
import ai.yda.framework.rag.core.generator.Generator;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);

        var retriever = new BaseRetriever(new VectorStore() {
            @Override
            public void add(List<Document> documents) {}

            @Override
            public Optional<Boolean> delete(List<String> idList) {
                return Optional.empty();
            }

            @Override
            public List<Document> similaritySearch(SearchRequest request) {
                return List.of();
            }
        });

        var augmenter = new BaseAugmenter();

        var generator = context.getBean(Generator.class);
        var rag = new BaseRagApplication(retriever, augmenter, generator);

        // Create HttpNettyChannel using factory
        var factory = new HttpNettyChannelFactory();
        var configuration = factory.buildConfiguration(
                "POST", "/channels", BaseAssistantRequest.class, BaseAssistantResponse.class);
        var channel = factory.createChannel(configuration);

        var assistant = new RagAssistant(rag, channel);
    }
}
