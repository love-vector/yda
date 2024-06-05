package ai.yda.framework.rag.core.model;

import java.util.List;
import java.util.Map;

public interface RagContext {

    List<String> getChunks();

    Map<String, Object> getMetadata();
}
