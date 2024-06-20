package ai.yda.framework.rag.retriever.filesystem.config;

import lombok.Getter;

import ai.yda.framework.rag.core.retriever.factory.RetrieverConfig;

@Getter
public enum WebsiteRetrieverConfig implements RetrieverConfig {
    LOCAL_DIRECTORY_PATH("localDirectoryPath");

    private final String key;

    WebsiteRetrieverConfig(String key) {
        this.key = key;
    }
}
