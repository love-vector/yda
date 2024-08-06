package ai.yda.common.shared.service;

import java.util.Optional;

public interface SessionProvider {

    Optional<String> getThreadId();

    void setThreadId(String threadId);
}
