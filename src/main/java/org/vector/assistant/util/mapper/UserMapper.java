package org.vector.assistant.util.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.vector.assistant.dto.CreateUserRequest;
import org.vector.assistant.dto.UserDto;
import org.vector.assistant.persistance.entity.UserEntity;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = UserMapper.NamedPasswordEncoder.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "password", source = "request.password", qualifiedByName = "encodePassword")
    UserEntity toEntity(final CreateUserRequest request);

    @Mapping(target = "email", source = "user.email")
    UserDto toDto(final UserEntity user);

    @Component
    @RequiredArgsConstructor
    class NamedPasswordEncoder {

        private final PasswordEncoder passwordEncoder;

        @Named("encodePassword")
        public String encodePassword(final String password) {
            return passwordEncoder.encode(password);
        }
    }
}
