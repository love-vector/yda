package ai.yda.framework.rag.generator;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Message {
    private String id;
    private String threadId;
    private String content;
    private OffsetDateTime createdAt;
}
