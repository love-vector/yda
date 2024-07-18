package ai.yda.common.shared.service;

import java.util.Optional;

public interface SessionProvider {

    void setSessionId(String sessionId);

    Optional<String> getThreadId();

    void setThreadId(String threadId);
}
