package ai.yda.framework.channel.http.config;

import lombok.Getter;

import ai.yda.common.shared.factory.FactoryConfig;

@Getter
public enum HttpChannelConfig implements FactoryConfig {
    METHOD("method"),
    URI("uri"),
    PORT("port");

    private final String key;

    HttpChannelConfig(String key) {
        this.key = key;
    }
}
