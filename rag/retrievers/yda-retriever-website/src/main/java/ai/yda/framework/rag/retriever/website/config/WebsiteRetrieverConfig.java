package ai.yda.framework.rag.retriever.website.config;

import lombok.Getter;

import ai.yda.common.shared.factory.FactoryConfig;

@Getter
public enum WebsiteRetrieverConfig implements FactoryConfig {
    WEBSITE_URL("url"),
    IS_CRAWLING_ENABLED("isCrawlingEnabled");

    private final String key;

    WebsiteRetrieverConfig(final String key) {
        this.key = key;
    }
}
