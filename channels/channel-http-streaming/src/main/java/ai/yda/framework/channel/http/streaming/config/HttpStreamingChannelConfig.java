package ai.yda.framework.channel.http.streaming.config;

import lombok.Getter;

import ai.yda.common.shared.factory.FactoryConfig;

@Getter
public enum HttpStreamingChannelConfig implements FactoryConfig {
    METHOD("method"),
    URI("uri"),
    PORT("port");

    private final String key;

    HttpStreamingChannelConfig(String key) {
        this.key = key;
    }
}
