package ai.yda;

import ai.yda.framework.rag.core.RagApplication;
import ai.yda.framework.rag.dto.RagRequest;
import ai.yda.rag.RagApp;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {


    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);


        var ragApp = new RagApp().run(new RagRequest() {});
    }
}