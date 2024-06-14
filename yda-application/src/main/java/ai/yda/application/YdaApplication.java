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

import ai.yda.framework.rag.base.application.BaseRagApplication;
import ai.yda.framework.rag.base.augmenter.BaseAugmenter;
import ai.yda.framework.rag.base.model.BaseRagRequest;
import ai.yda.framework.rag.base.retriever.BaseRetriever;
import ai.yda.framework.rag.core.generator.Generator;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);

        //        var milvusVectorStore = context.getBean(VectorStore.class);
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
        var application = new BaseRagApplication(retriever, augmenter, generator);

        System.out.println(application
                .doRag(BaseRagRequest.builder().content("Test Message").build())
                .getContent());
    }
}
