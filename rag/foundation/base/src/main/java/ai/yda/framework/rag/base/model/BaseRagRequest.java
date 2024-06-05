package ai.yda.framework.rag.base.model;

import lombok.Builder;
import lombok.Getter;

import ai.yda.framework.rag.core.model.RagRequest;

@Getter
@Builder(toBuilder = true)
public class BaseRagRequest implements RagRequest {

    private String content;
}
