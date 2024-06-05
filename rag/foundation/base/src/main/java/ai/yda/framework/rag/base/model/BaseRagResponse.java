package ai.yda.framework.rag.base.model;

import lombok.Builder;
import lombok.Getter;

import ai.yda.framework.rag.core.model.RagResponse;

@Getter
@Builder(toBuilder = true)
public class BaseRagResponse implements RagResponse {

    private String content;
}
