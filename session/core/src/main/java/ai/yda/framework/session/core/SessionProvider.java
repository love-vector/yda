package ai.yda.framework.session.core;

import java.util.Optional;

public interface SessionProvider {

    Optional<String> getThreadId();

    void setThreadId(String threadId);
}
