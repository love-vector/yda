package ai.yda.common.shared.service.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import ai.yda.common.shared.service.SessionProvider;

public class ThreadLocalSessionProvider implements SessionProvider {

    private final Map<String, String> sessionThreadMap = new ConcurrentHashMap<>();

    // TODO: need to rework into Redis or smth
    private final ThreadLocal<String> threadLocalSessionId = new ThreadLocal<>();

    public void setThreadId(final String threadId) {
        sessionThreadMap.put(threadLocalSessionId.get(), threadId);
    }

    public Optional<String> getThreadId() {
        return Optional.ofNullable(sessionThreadMap.get(threadLocalSessionId.get()));
    }

    @Override
    public void setSessionId(final String sessionId) {
        threadLocalSessionId.set(sessionId);
    }
}
