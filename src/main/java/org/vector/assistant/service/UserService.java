package org.vector.assistant.service;

import java.util.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.dto.CreateUserRequest;
import org.vector.assistant.entity.UserEntity;
import org.vector.assistant.repository.UserRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<ResponseEntity<String>> createUser(final CreateUserRequest request) {
        return userRepository
                .findByUsername(request.name())
                .map(user -> ResponseEntity.badRequest().body("User already exists"))
                .switchIfEmpty(Mono.defer(() -> {
                    var user = UserEntity.builder()
                            .username(request.name())
                            .password(passwordEncoder.encode(request.password()))
                            .build();
                    return userRepository
                            .save(user)
                            .then(Mono.just(
                                    ResponseEntity.status(HttpStatus.CREATED).body("User created")));
                }));
    }

    public Flux<String> findAllUserNames() {
        return userRepository.findAll().map(UserEntity::getUsername);
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("User not found"))))
                .map(user -> org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(new ArrayList<>())
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build());
    }
}
