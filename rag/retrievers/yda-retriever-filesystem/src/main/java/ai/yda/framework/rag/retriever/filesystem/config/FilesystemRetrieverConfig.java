package ai.yda.framework.rag.retriever.filesystem.config;

import lombok.Getter;

import ai.yda.framework.rag.core.retriever.factory.RetrieverConfig;

@Getter
public enum FilesystemRetrieverConfig implements RetrieverConfig {
    LOCAL_DIRECTORY_PATH("localDirectoryPath");

    private final String key;

    FilesystemRetrieverConfig(String key) {
        this.key = key;
    }
}
