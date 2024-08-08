package ai.yda.framework.assistant.rag.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.core.assistant.RagAssistant;
import ai.yda.framework.rag.core.Rag;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

@AutoConfiguration
public class RagAssistantAutoConfiguration {

    @Bean
    public RagAssistant ragAssistant(final Rag<RagRequest, RagResponse> rag) {
        return new RagAssistant(rag);
    }
}
