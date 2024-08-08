package ai.yda.framework.session.core;

import java.util.Optional;

public class ThreadLocalSessionProvider implements SessionProvider {

    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public void setThreadId(final String threadId) {
        threadLocal.set(threadId);
    }

    public Optional<String> getThreadId() {
        return Optional.ofNullable(threadLocal.get());
    }
}
