package org.vector.yda.util.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.vector.yda.model.dto.UserDto;
import org.vector.yda.model.request.CreateUserRequest;
import org.vector.yda.persistance.entity.UserEntity;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = UserMapper.NamedPasswordEncoder.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "request.password", qualifiedByName = "encodePassword")
    UserEntity createEntity(final CreateUserRequest request);

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
