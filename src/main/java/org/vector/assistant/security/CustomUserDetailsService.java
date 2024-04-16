package org.vector.assistant.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.exception.not.found.UserNotFoundException;
import org.vector.assistant.exception.unauthorized.UserUnauthorizedException;
import org.vector.assistant.persistance.dao.UserDao;
import org.vector.assistant.persistance.entity.UserEntity;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    private final MessageSourceAccessor messageSourceAccessor = SpringSecurityMessageSource.getAccessor();

    /**
     * Loads the user details by the email provided as username. This method is used by Spring Security
     * during the authentication process. If the user is not found, it throws a {@link UsernameNotFoundException}.
     *
     * @param username the email of the user to load.
     * @return the UserDetails of the user found by email.
     * @throws UsernameNotFoundException if no user is found or has no GrantedAuthority for the provided email.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            return userDao.getUserByEmail(username);
        } catch (final UserNotFoundException ignored) {
            throw new UsernameNotFoundException(messageSourceAccessor.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     * If no authentication is present, or the principal is not a UserEntity, it throws an UnauthorizedException.
     *
     * @return the UserEntity representing the currently authenticated user.
     * @throws UserUnauthorizedException if the user is not properly authenticated.
     */
    public UserEntity getAuthorizedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() instanceof String) {
            throw new UserUnauthorizedException();
        }
        return (UserEntity) authentication.getPrincipal();
    }
}
