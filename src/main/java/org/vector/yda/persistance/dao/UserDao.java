package org.vector.yda.persistance.dao;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.exception.not.found.UserNotFoundException;
import org.vector.yda.persistance.entity.UserEntity;
import org.vector.yda.persistance.repository.UserRepository;

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
