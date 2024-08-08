package ai.yda.framework.channel.rest.spring.sync.session;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import ai.yda.framework.session.core.SessionProvider;

@Component
@RequiredArgsConstructor
public class RestSyncSessionProvider implements SessionProvider {

    @Override
    public void put(final String key, final Object value) {
        RequestContextHolder.currentRequestAttributes().setAttribute(key, value, RequestAttributes.SCOPE_SESSION);
    }

    @Override
    public Optional<Object> get(final String key) {
        return Optional.ofNullable(
                RequestContextHolder.currentRequestAttributes().getAttribute(key, RequestAttributes.SCOPE_SESSION));
    }
}
