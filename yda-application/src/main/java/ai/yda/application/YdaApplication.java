package ai.yda.application;

import ai.yda.framework.rag.base.application.BaseRagApplication;
import ai.yda.framework.rag.base.augmenter.BaseAugmenter;
import ai.yda.framework.rag.base.generator.BaseGenerator;
import ai.yda.framework.rag.base.retriever.BaseRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

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

        new BaseRagApplication(retriever, augmenter, generator);

    }
}
