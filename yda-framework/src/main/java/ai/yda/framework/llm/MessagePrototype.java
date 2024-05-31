package ai.yda.framework.llm;

import java.time.OffsetDateTime;

public interface MessagePrototype {

    String getId();

    String getThreadId();

    String getContent();

    OffsetDateTime getCreatedAt();
}
