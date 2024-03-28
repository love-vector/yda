package org.vector.assistant.persistance.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.persistance.entity.UserEntity;
import org.vector.assistant.persistance.repository.UserRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class UserDao {

    private final UserRepository userRepository;

    public Mono<UserEntity> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<UserEntity> createUser(final UserEntity user) {
        return userRepository.save(user.toBuilder().isNew(Boolean.TRUE).build());
    }

    public Mono<Boolean> existsByEmail(final String email) {
        return userRepository.existsByEmail(email);
    }
}
