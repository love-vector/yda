package ai.yda.framework.rag.retriever.filesystem.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import ai.yda.framework.rag.retriever.RetrieverProperties;

@Setter
@Getter
@ConfigurationProperties(RetrieverFilesystemProperties.CONFIG_PREFIX)
public class RetrieverFilesystemProperties extends RetrieverProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.retriever.filesystem";

    private String fileStoragePath;
}
