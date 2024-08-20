/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.channel.shared;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

import lombok.Getter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;

/**
 * Represents a bearer token based Authentication.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Getter
public class TokenAuthentication extends AbstractAuthenticationToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Multiplier used in hash code calculations to ensure uniqueness.
     */
    private static final Short HASH_CODE_MULTIPLIER = 31;

    /**
     * The principal (e.g., username) associated with the authentication token.
     */
    private final Object principal;

    /**
     * A hash of the key, used to verify the integrity of the token.
     */
    private final int keyHash;

    /**
     * Constructs a new {@link TokenAuthentication} instance with the specified key.
     * The token itself is not stored in this instance; instead, only its hash is stored.
     * The principal is set to "anonymousUser" and the authorities are set to "ROLE_ANONYMOUS".
     *
     * @param key the authentication key.
     */
    public TokenAuthentication(final String key) {
        this(extractKeyHash(key), "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }

    /**
     * Constructs new {@link TokenAuthentication} instance with the specified key, principal, and authorities.
     *
     * @param key         the authentication key.
     * @param principal   the principal (e.g., username).
     * @param authorities the authorities granted to the principal.
     */
    public TokenAuthentication(
            final String key, final Object principal, final Collection<? extends GrantedAuthority> authorities) {
        this(extractKeyHash(key), principal, authorities);
    }

    /**
     * Constructs new {@link TokenAuthentication} with the specified key hash, principal, and authorities. The token is
     * set as authenticated.
     *
     * @param keyHash     the hash of the authentication key.
     * @param principal   the principal (e.g., username).
     * @param authorities the authorities granted to the principal.
     */
    private TokenAuthentication(
            final Integer keyHash, final Object principal, final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.isTrue(principal != null && !"".equals(principal), "principal cannot be null or empty");
        Assert.notEmpty(authorities, "authorities cannot be null or empty");
        this.keyHash = keyHash;
        this.principal = principal;
        setAuthenticated(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof TokenAuthentication test) {
            return this.getKeyHash() == test.getKeyHash();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = TokenAuthentication.HASH_CODE_MULTIPLIER * result + this.keyHash;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getCredentials() {
        return keyHash;
    }

    /**
     * Extracts the hash code of the specified key.
     *
     * @param key the key for which to extract the hash code.
     * @return the hash code of the key.
     */
    public static Integer extractKeyHash(final String key) {
        Assert.hasLength(key, "key cannot be empty or null");
        return key.hashCode();
    }
}
