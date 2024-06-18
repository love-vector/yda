package ai.yda.framework.rag.core.model;

import java.util.List;
import java.util.Map;

public interface RagContext<KNOWLEDGE> {

    List<KNOWLEDGE> getKnowledge();

    Map<String, Object> getMetadata();
}
