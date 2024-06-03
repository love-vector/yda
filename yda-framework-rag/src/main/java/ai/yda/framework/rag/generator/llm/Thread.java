package ai.yda.framework.rag.generator.llm;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Thread {
    private String id;
    private OffsetDateTime createdAt;
}
