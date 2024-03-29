package org.vector.assistant.service;

import java.net.URI;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.exception.conflict.UserAlreadyExistsException;
import org.vector.assistant.model.dto.UserDto;
import org.vector.assistant.model.request.CreateUserRequest;
import org.vector.assistant.persistance.dao.UserDao;
import org.vector.assistant.security.UserDetailsService;
import org.vector.assistant.util.mapper.UserMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserDetailsService userDetailsService;

    private final UserDao userDao;

    private final UserMapper userMapper;

    public Mono<URI> createUser(final CreateUserRequest request) {
        return userDao.existsByEmail(request.email()).flatMap(exists -> {
            if (exists) {
                return Mono.error(new UserAlreadyExistsException());
            }
            return userDao.createUser(userMapper.toEntity(request))
                    .map(user -> URI.create(Objects.requireNonNull(user.getId()).toString()));
        });
    }

    public Mono<UserDto> getCurrentUser() {
        return userDetailsService.getAuthorizedUser().map(userMapper::toDto);
    }
}
