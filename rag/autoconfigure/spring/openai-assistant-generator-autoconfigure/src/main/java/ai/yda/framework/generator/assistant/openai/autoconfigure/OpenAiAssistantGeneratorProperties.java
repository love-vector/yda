package ai.yda.framework.generator.assistant.openai.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(OpenAiAssistantGeneratorProperties.CONFIG_PREFIX)
public class OpenAiAssistantGeneratorProperties {

    public static final String CONFIG_PREFIX = "yda.ai.framework.generator.assistant.openai";

    private String assistantId;

    private String apiKey;
}
