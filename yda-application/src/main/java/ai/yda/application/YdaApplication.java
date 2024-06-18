package ai.yda.application;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.assistant.RagAssistant;
import ai.yda.framework.core.channel.factory.netty.HttpNettyChannelFactory;
import ai.yda.framework.rag.base.application.BaseRagApplication;
import ai.yda.framework.rag.base.augmenter.BaseAugmenter;
import ai.yda.framework.rag.base.augmenter.BaseChainAugmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.retriever.Retriever;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);

        var chainAugmenter = new BaseChainAugmenter();
        chainAugmenter.addAugmenter(new BaseAugmenter());

        var retriever = context.getBean(Retriever.class);
        var generator = context.getBean(Generator.class);

        var rag = new BaseRagApplication(retriever, chainAugmenter, generator);

        // Create HttpNettyChannel using factory
        var factory = new HttpNettyChannelFactory();
        var configuration = factory.buildConfiguration(
                "POST", "/channels", BaseAssistantRequest.class, BaseAssistantResponse.class);
        var channel = factory.createChannel(configuration);

        var assistant = new RagAssistant(rag, channel);
    }
}
