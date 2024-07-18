package ai.yda.framework.channel.netty.config;

import lombok.Getter;

import ai.yda.common.shared.factory.FactoryConfig;

@Getter
public enum NettyChannelConfig implements FactoryConfig {
    METHOD("method"),
    URI("uri"),
    PORT("port");

    private final String key;

    NettyChannelConfig(final String key) {
        this.key = key;
    }
}
