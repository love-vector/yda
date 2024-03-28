package org.vector.assistant.controller;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.dto.CreateUserRequest;
import org.vector.assistant.dto.UserDto;
import org.vector.assistant.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<URI>> createUser(@RequestBody @Validated final CreateUserRequest request) {
        return userService.createUser(request).map(ResponseEntity::ok);
    }

    @GetMapping("/authorized")
    public Mono<ResponseEntity<UserDto>> getAuthorizedUser() {
        return userService.getAuthorizedUser().map(ResponseEntity::ok);
    }
}
