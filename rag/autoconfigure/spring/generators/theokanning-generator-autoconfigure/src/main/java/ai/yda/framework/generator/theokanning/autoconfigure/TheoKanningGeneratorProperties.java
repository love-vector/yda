package ai.yda.framework.generator.theokanning.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(TheoKanningGeneratorProperties.CONFIG_PREFIX)
public class TheoKanningGeneratorProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.generator.theokanning.autoconfigure";

    private String apiKey;
}
