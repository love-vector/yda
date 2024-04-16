package org.vector.assistant.web.controller;

import java.net.URI;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.model.dto.UserDto;
import org.vector.assistant.model.request.CreateUserRequest;
import org.vector.assistant.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @SecurityRequirements
    @PostMapping
    public ResponseEntity<URI> createUser(@RequestBody @Validated final CreateUserRequest request) {
        return ResponseEntity.created(userService.createUser(request)).build();
    }

    @GetMapping("/authorized")
    public ResponseEntity<UserDto> getAuthorizedUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }
}
