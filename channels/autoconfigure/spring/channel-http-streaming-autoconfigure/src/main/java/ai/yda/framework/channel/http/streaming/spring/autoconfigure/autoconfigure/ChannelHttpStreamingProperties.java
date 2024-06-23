package ai.yda.framework.channel.http.streaming.spring.autoconfigure.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(ChannelHttpStreamingProperties.CONFIG_PREFIX)
public class ChannelHttpStreamingProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.channel.http.streaming";

    private String method;
    private String uri;
    private String port;
}
