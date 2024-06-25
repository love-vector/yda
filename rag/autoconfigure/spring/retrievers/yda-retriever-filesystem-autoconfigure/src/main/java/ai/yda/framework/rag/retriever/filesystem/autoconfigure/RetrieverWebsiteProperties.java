package ai.yda.framework.rag.retriever.filesystem.autoconfigure;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(RetrieverWebsiteProperties.CONFIG_PREFIX)
public class RetrieverWebsiteProperties {
    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.retriever.website";

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
