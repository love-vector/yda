package ai.yda;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import ai.yda.framework.llm.LlmProvider;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    private final ApplicationContext applicationContext;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);
        LlmProvider llmProvider = context.getBean(LlmProvider.class);
    }
}
