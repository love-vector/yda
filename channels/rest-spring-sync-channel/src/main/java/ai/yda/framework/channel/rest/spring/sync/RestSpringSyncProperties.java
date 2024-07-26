package ai.yda.framework.channel.rest.spring.sync;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(RestSpringSyncProperties.CONFIG_PREFIX)
public class RestSpringSyncProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.channel.rest.spring.sync";

    private String endpointRelativePath;

    private String securityToken;
}
