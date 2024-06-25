package ai.yda.framework.rag.retriever.filesystem.config;

import lombok.Getter;

import ai.yda.common.shared.factory.FactoryConfig;

@Getter
public enum WebsiteRetrieverConfig implements FactoryConfig {
    ;

    private final String key;

    WebsiteRetrieverConfig(String key) {
        this.key = key;
    }
}
