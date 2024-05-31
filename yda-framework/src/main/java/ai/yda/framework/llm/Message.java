package ai.yda.framework.llm;

import java.time.OffsetDateTime;

public interface Message {

    String getId();

    String getThreadId();

    String getContent();

    OffsetDateTime getCreatedAt();
}
