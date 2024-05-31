package ai.yda.framework.llm;

import java.time.OffsetDateTime;

public interface Assistant {

    String getId();

    String getName();

    String getInstructions();

    OffsetDateTime getCreatedAt();
}
