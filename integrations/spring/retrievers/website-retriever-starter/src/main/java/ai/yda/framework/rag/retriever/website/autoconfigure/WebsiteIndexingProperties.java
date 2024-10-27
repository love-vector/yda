package ai.yda.framework.rag.retriever.website.autoconfigure;

import ai.yda.framework.rag.retriever.shared.RetrieverProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(WebsiteIndexingProperties.CONFIG_PREFIX)
public class WebsiteIndexingProperties extends RetrieverProperties {

    public static final String CONFIG_PREFIX = RetrieverWebsiteProperties.CONFIG_PREFIX + ".node.indexing";

    public WebsiteIndexingProperties() {
    }
}
