package ai.yda.framework.llm;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Assistant {
    private String id;
    private String name;
    private String instructions;
    private OffsetDateTime createdAt;
}
