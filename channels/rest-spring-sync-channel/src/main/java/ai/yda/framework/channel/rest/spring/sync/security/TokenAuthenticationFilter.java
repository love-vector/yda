package ai.yda.framework.channel.rest.spring.sync.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    private final AuthenticationConverter authenticationConverter = new TokenAuthenticationConverter();

    private final TokenAuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(final String token) {
        this.authenticationManager = new TokenAuthenticationManager(token);
    }

    @Override
    protected void doFilterInternal(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var authRequest = this.authenticationConverter.convert(request);
            if (authRequest == null) {
                this.logger.trace("Did not process authentication request since failed"
                        + " to find token in Bearer Authorization header");
                filterChain.doFilter(request, response);
                return;
            }
            if (authenticationIsRequired(authRequest)) {
                var authResult = authenticationManager.authenticate(authRequest);
                securityContextHolderStrategy.getContext().setAuthentication(authResult);
            }
        } catch (final AuthenticationException ignored) {
        }
        filterChain.doFilter(request, response);
    }

    protected boolean authenticationIsRequired(final Authentication authentication) {
        // Only reauthenticate if token doesn't match SecurityContextHolder and user
        // isn't authenticated (see SEC-53)
        var currentAuthentication =
                this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentAuthentication == null
                || !currentAuthentication.getCredentials().equals(authentication.getCredentials())
                || !currentAuthentication.isAuthenticated()) {
            return true;
        }
        return (currentAuthentication instanceof AnonymousAuthenticationToken);
    }
}
