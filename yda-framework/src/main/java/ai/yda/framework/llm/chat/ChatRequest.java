package ai.yda.framework.llm.chat;

import lombok.NonNull;

public record ChatRequest(@NonNull String assistantId, String threadId, @NonNull String message) {}
