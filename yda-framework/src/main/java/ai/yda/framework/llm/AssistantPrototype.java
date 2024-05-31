package ai.yda.framework.llm;

import java.time.OffsetDateTime;

public interface AssistantPrototype {

    String getId();

    String getName();

    String getInstructions();

    OffsetDateTime getCreatedAt();
}
