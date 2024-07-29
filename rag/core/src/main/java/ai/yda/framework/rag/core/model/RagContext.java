package ai.yda.framework.rag.core.model;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class RagContext {

    private List<String> knowledge;

    private Map<String, Object> metadata;
}
