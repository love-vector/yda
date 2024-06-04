package ai.yda.application;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import ai.yda.framework.rag.base.application.BaseRagApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(YdaApplication.class, args);

        new BaseRagApplication();
    }
}
