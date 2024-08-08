package ai.yda.framework.session.core;

import java.util.Optional;

public interface SessionProvider {

    void put(String key, Object value);

    Optional<Object> get(String key);
}
