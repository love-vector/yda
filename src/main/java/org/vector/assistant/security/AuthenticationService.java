package org.vector.assistant.security;

import java.util.UUID;

import reactor.core.publisher.Mono;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.vector.assistant.persistance.entity.UserEntity;

@Service
public class AuthenticationService {

    public Mono<UUID> getUserId() {
        return getUserDetails().map(userDetails -> ((UserEntity) userDetails).getId());
    }

    private Mono<UserDetails> getUserDetails() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth.getPrincipal() instanceof UserDetails)
                .map(auth -> (UserDetails) auth.getPrincipal());
    }
}
