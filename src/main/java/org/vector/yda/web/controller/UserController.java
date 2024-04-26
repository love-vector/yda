package org.vector.yda.web.controller;

import java.net.URI;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.yda.model.dto.UserDto;
import org.vector.yda.model.request.CreateUserRequest;
import org.vector.yda.service.UserService;

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
