package org.vector.assistant.persistance.entity;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table(name = "users", schema = "chatbot")
@Getter
@Builder(toBuilder = true)
public class UserEntity implements UserDetails, Persistable<UUID> {

    @Id
    @Column("id")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Builder.Default
    @Transient
    private Boolean isNew = Boolean.FALSE;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
