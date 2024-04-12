package org.vector.assistant.service;

import java.net.URI;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.dto.CreateUserRequest;
import org.vector.assistant.dto.UserDto;
import org.vector.assistant.exception.conflict.UserAlreadyExistsException;
import org.vector.assistant.persistance.dao.UserDao;
import org.vector.assistant.persistance.entity.UserEntity;
import org.vector.assistant.util.mapper.UserMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

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

    public Mono<UserDto> getAuthorizedUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext ->
                        (UserEntity) securityContext.getAuthentication().getPrincipal())
                .map(userMapper::toDto);
    }
}
