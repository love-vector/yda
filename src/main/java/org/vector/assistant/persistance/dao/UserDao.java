package org.vector.assistant.persistance.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.exception.not.found.UserNotFoundException;
import org.vector.assistant.persistance.entity.UserEntity;
import org.vector.assistant.persistance.repository.UserRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class UserDao {

    private final UserRepository userRepository;

    public UserEntity getUserByEmail(final String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public UserEntity createUser(final UserEntity user) {
        return userRepository.save(user);
    }
}
