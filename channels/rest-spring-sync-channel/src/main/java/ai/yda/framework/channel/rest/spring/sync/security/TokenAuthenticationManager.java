package ai.yda.framework.channel.rest.spring.sync.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class TokenAuthenticationManager implements AuthenticationManager {

    private final Integer tokenKeyHash;

    public TokenAuthenticationManager(final String token) {
        this.tokenKeyHash = TokenAuthentication.extractKeyHash(token);
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        if (tokenKeyHash.equals(authentication.getCredentials())) {
            return authentication;
        }
        throw new TokenAuthenticationException();
    }
}
