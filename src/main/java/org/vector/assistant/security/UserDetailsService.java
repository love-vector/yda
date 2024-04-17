package org.vector.assistant.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.persistance.dao.UserDao;
import org.vector.assistant.persistance.entity.UserEntity;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsService implements ReactiveUserDetailsService {

    private final UserDao userDao;

    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return userDao.getUserByEmail(username).map(user -> user);
    }

    public Mono<UserEntity> getAuthorizedUser() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext ->
                (UserEntity) securityContext.getAuthentication().getPrincipal());
    }
}
