package ai.yda.framework.azure.provider.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(AzureProviderProperties.CONFIG_PREFIX)
public class AzureProviderProperties {

	public static final String CONFIG_PREFIX = "yda.ai.azure.provider";

	private String model;

	private String apiKey;
}
