package ai.yda.framework.rag.core.model.impl;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

import ai.yda.framework.rag.core.model.RagContext;

@Getter
@Builder(toBuilder = true)
public class BaseRagContext implements RagContext<String> {

    private List<String> knowledge;

    private Map<String, Object> metadata;
}
