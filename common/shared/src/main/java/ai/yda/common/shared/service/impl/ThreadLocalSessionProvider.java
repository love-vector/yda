package ai.yda.common.shared.service.impl;

import java.util.Optional;

import ai.yda.common.shared.service.SessionProvider;

public class ThreadLocalSessionProvider implements SessionProvider {

    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public void setThreadId(final String threadId) {
        threadLocal.set(threadId);
    }

    public Optional<String> getThreadId() {
        return Optional.ofNullable(threadLocal.get());
    }
}
