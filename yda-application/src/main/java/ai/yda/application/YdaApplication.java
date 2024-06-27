package ai.yda.application;

import ai.yda.framework.core.assistant.RagAssistant;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.base.application.BaseRagApplication;
import ai.yda.framework.rag.base.augmenter.BaseAugmenter;
import ai.yda.framework.rag.base.augmenter.BaseChainAugmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.website.autoconfigure.RetrieverWebsiteProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);
        var retrieverWebsite = context.getBean(RetrieverWebsiteProperties.class);
        retrieverWebsite.setUrl("https://dog.ceo/dog-api/");
        var chainAugmenter = new BaseChainAugmenter();
        chainAugmenter.addAugmenter(new BaseAugmenter());
        var retriever = context.getBean(Retriever.class);
        var generator = context.getBean(Generator.class);

        var rag = new BaseRagApplication(retriever, chainAugmenter, generator);

        var channel = context.getBean(Channel.class);

        var assistant = new RagAssistant(rag, List.of(channel));
        //        var assistant = new RagAssistantReactive(rag, List.of(channel));
    }
}
