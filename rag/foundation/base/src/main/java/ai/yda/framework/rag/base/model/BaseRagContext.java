package ai.yda.framework.rag.base.model;

import ai.yda.framework.rag.core.model.RagContext;

import java.util.List;
import java.util.Map;

public class BaseRagContext implements RagContext {

    private List<String> chunks;
    private Map<String, Object> metadata;

    @Override
    public List<String> getChunks() {
        return chunks;
    }

    public void setChunks(List<String> chunks) {
        this.chunks = chunks;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
