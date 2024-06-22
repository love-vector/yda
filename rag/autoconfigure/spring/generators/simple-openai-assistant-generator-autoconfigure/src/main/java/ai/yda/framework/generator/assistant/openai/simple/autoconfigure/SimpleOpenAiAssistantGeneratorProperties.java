package ai.yda.framework.generator.assistant.openai.simple.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(SimpleOpenAiAssistantGeneratorProperties.CONFIG_PREFIX)
public class SimpleOpenAiAssistantGeneratorProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.generator.assistant.openai.simple.autoconfigure";

    private String apiKey;
}
