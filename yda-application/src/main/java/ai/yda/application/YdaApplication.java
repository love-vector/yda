package ai.yda.application;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.openai.OpenAiChatModel;
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
import ai.yda.framework.rag.base.generator.BaseGenerator;
import ai.yda.framework.rag.base.retriever.BaseRetriever;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);

        var milvusVectorStore = context.getBean(VectorStore.class);
        var retriever = new BaseRetriever(milvusVectorStore);

        var augmenter = new BaseAugmenter();

        var chatModel = context.getBean(OpenAiChatModel.class);
        var generator = new BaseGenerator(chatModel);

        var rag = new BaseRagApplication(retriever, augmenter, generator);

        // Create HttpNettyChannel using factory
        var factory = new HttpNettyChannelFactory();
        var configuration = factory.buildConfiguration(
                "POST", "/channels", BaseAssistantRequest.class, BaseAssistantResponse.class);
        var channel = factory.createChannel(configuration);

        var assistant = new RagAssistant(rag, channel);

        System.out.println(
                rag.doRag(BaseAssistantRequest.builder().content("Test Message").build())
                        .getContent());
    }
}
