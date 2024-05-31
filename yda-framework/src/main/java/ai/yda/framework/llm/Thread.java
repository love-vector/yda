package ai.yda.framework.llm;

import java.time.OffsetDateTime;

public interface Thread {

    String getId();

    OffsetDateTime getCreatedAt();
}