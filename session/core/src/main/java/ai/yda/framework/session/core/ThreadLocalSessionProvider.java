package ai.yda.framework.session.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThreadLocalSessionProvider implements SessionProvider {

    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Override
    public void put(final String key, final Object value) {
        var threadLocalMap = threadLocal.get();
        if (threadLocalMap == null) {
            threadLocalMap = new HashMap<>();
            threadLocal.set(threadLocalMap);
        }
        threadLocalMap.put(key, value);
    }

    @Override
    public Optional<Object> get(final String key) {
        var threadLocalMap = threadLocal.get();
        return threadLocalMap == null ? Optional.empty() : Optional.ofNullable(threadLocalMap.get(key));
    }
}
