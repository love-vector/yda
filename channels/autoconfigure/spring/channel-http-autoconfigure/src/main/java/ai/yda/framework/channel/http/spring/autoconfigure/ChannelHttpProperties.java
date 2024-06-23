package ai.yda.framework.channel.http.spring.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(ChannelHttpProperties.CONFIG_PREFIX)
public class ChannelHttpProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.channel.http";

    private String method;
    private String uri;
    private String port;
}
