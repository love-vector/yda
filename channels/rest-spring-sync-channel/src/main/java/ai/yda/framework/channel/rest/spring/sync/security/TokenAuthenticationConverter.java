package ai.yda.framework.channel.rest.spring.sync.security;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class TokenAuthenticationConverter implements AuthenticationConverter {

    private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

    private static final Short TOKEN_START_POSITION = 7;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    @Override
    public Authentication convert(final HttpServletRequest request) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return null;
        }
        authHeader = authHeader.trim();
        if (!StringUtils.startsWithIgnoreCase(authHeader, AUTHENTICATION_SCHEME_BEARER)) {
            return null;
        }
        if (authHeader.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
            throw new BadCredentialsException("Empty bearer authentication token");
        }
        var token = authHeader.substring(TOKEN_START_POSITION);
        var currentAuthentication = securityContextHolderStrategy.getContext().getAuthentication();
        return new TokenAuthentication(
                token, currentAuthentication.getPrincipal(), currentAuthentication.getAuthorities());
    }
}
