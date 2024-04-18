package org.vector.assistant.service;

import java.net.URI;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.model.dto.UserDto;
import org.vector.assistant.model.request.CreateUserRequest;
import org.vector.assistant.persistance.dao.UserDao;
import org.vector.assistant.security.CustomUserDetailsService;
import org.vector.assistant.util.mapper.UserMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final CustomUserDetailsService customUserDetailsService;

    private final UserDao userDao;

    private final UserMapper userMapper;

    /**
     * Creates a new user based on the provided request data and returns the URI of the newly created user.
     *
     * @param request the data used to create a new user.
     * @return the URI of the newly created user.
     */
    public URI createUser(final CreateUserRequest request) {
        var user = userDao.createUser(userMapper.toEntity(request));
        return URI.create(Objects.requireNonNull(user.getId()).toString());
    }

    /**
     * Retrieves the currently authenticated user's details as a DTO.
     *
     * @return the DTO representing the currently authenticated user.
     */
    public UserDto getCurrentUser() {
        return userMapper.toDto(customUserDetailsService.getAuthorizedUser());
    }
}
