package ai.yda.framework.rag.retriever.filesystem.config;

import lombok.Getter;

import ai.yda.common.shared.factory.FactoryConfig;

@Getter
public enum FilesystemRetrieverConfig implements FactoryConfig {
    LOCAL_DIRECTORY_PATH("localDirectoryPath");

    private final String key;

    FilesystemRetrieverConfig(final String key) {
        this.key = key;
    }
}
