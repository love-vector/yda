package ai.yda.framework.channel.rest.spring.sync.security;

import org.springframework.security.core.AuthenticationException;

public class TokenAuthenticationException extends AuthenticationException {

    private static final String MESSAGE = "Invalid token";

    public TokenAuthenticationException() {
        super(MESSAGE);
    }
}
