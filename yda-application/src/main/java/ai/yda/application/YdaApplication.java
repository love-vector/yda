package ai.yda.application;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class YdaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YdaApplication.class, args);
    }
}
