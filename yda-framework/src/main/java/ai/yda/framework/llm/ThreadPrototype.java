package ai.yda.framework.llm;

import java.time.OffsetDateTime;

public interface ThreadPrototype {

    String getId();

    OffsetDateTime getCreatedAt();
}
