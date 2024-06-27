package ai.yda.framework.rag.retriever.website.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
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

    private String url;
}
