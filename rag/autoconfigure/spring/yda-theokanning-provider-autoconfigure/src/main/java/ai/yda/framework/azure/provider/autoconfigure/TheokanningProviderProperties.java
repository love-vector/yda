package ai.yda.framework.azure.provider.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(TheokanningProviderProperties.CONFIG_PREFIX)
public class TheokanningProviderProperties {

    public static final String CONFIG_PREFIX = "yda.ai.theokanning.provider";

    private String model;

    private String apiKey;
}
