package ai.yda.framework.channel.rest.spring.sync;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(RestSpringSyncProperties.CONFIG_PREFIX)
public class RestSpringSyncProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.channel.rest.spring.sync";

    public static final String DEFAULT_ENDPOINT_RELATIVE_PATH = "/";

    private String endpointRelativePath = RestSpringSyncProperties.DEFAULT_ENDPOINT_RELATIVE_PATH;

    private String securityToken;
}
