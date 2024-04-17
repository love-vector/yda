package org.vector.yda.service;

import java.net.URI;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.model.dto.UserDto;
import org.vector.yda.model.request.CreateUserRequest;
import org.vector.yda.persistance.dao.UserDao;
import org.vector.yda.security.UserDetailsService;
import org.vector.yda.util.mapper.UserMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserDetailsService userDetailsService;

    private final UserDao userDao;

    private final UserMapper userMapper;

    /**
     * Creates a new user based on the provided request data and returns the URI of the newly created user.
     *
     * @param request the data used to create a new user.
     * @return the URI of the newly created user.
     */
    public URI createUser(final CreateUserRequest request) {
        var user = userDao.createUser(userMapper.createEntity(request));
        return URI.create(Objects.requireNonNull(user.getId()).toString());
    }

    /**
     * Retrieves the currently authenticated user's details as a DTO.
     *
     * @return the DTO representing the currently authenticated user.
     */
    public UserDto getCurrentUser() {
        return userMapper.toDto(userDetailsService.getAuthorizedUser());
    }
}
