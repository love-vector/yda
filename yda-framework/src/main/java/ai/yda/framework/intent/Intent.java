package ai.yda.framework.intent;

import lombok.NonNull;

public record Intent(
        @NonNull String name,
        @NonNull String definition,
        String description) {}
