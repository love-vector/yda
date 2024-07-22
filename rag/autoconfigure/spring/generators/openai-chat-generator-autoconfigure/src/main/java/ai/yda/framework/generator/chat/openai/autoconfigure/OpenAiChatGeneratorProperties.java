package ai.yda.framework.generator.chat.openai.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(OpenAiChatGeneratorProperties.CONFIG_PREFIX)
public class OpenAiChatGeneratorProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.generator.chat.openai";

    private String apiKey;
}
