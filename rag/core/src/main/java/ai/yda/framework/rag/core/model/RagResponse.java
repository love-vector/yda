package ai.yda.framework.rag.core.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class RagResponse {

    private String result;
}
