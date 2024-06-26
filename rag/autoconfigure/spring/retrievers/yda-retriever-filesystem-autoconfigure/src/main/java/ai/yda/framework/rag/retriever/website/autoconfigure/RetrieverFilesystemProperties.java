package ai.yda.framework.rag.retriever.website.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(RetrieverFilesystemProperties.CONFIG_PREFIX)
public class RetrieverFilesystemProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.retriever.filesystem";

    private String localDirectoryPath;

    private String databaseName;

    private String collectionName;

    private Integer embeddingDimension;

    private String openAiKey;

    private String openAiModel;

    private String username;

    private String password;

    private String host;
}
