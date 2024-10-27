package ai.yda.framework.rag.retriever.website.autoconfigure;

import ai.yda.framework.rag.retriever.shared.RetrieverProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(DataFlowCoordinatorProperties.CONFIG_PREFIX)
public class DataFlowCoordinatorProperties extends RetrieverProperties {


    /**
     * The configuration prefix used to reference properties related to the website Retriever in application
     * configurations. This prefix is used for binding properties within the particular namespace.
     */
    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.retriever.website" + ".data.coordinator";

    /**
     * Operational URL of website or sitemap.
     */
    private String url;


    public DataFlowCoordinatorProperties() {
    }


}
