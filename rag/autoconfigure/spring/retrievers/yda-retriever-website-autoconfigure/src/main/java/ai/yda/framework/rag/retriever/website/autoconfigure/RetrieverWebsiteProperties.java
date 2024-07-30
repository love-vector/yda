package ai.yda.framework.rag.retriever.website.autoconfigure;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import ai.yda.framework.rag.retriever.RetrieverProperties;

@Getter
@Setter
@ConfigurationProperties(RetrieverWebsiteProperties.CONFIG_PREFIX)
public class RetrieverWebsiteProperties extends RetrieverProperties {
    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.retriever.website";

    private String url;

    private boolean isCrawlingEnabled;
}
