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

@Getter
public class TokenAuthentication extends AbstractAuthenticationToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Short HASH_CODE_MULTIPLIER = 31;

    private final Object principal;

    private final int keyHash;

    public TokenAuthentication(final String key) {
        this(extractKeyHash(key), "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }

    public TokenAuthentication(
            final String key, final Object principal, final Collection<? extends GrantedAuthority> authorities) {
        this(extractKeyHash(key), principal, authorities);
    }

    private TokenAuthentication(
            final Integer keyHash, final Object principal, final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.isTrue(principal != null && !"".equals(principal), "principal cannot be null or empty");
        Assert.notEmpty(authorities, "authorities cannot be null or empty");
        this.keyHash = keyHash;
        this.principal = principal;
        setAuthenticated(true);
    }

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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = TokenAuthentication.HASH_CODE_MULTIPLIER * result + this.keyHash;
        return result;
    }

    @Override
    public Object getCredentials() {
        return keyHash;
    }

    public static Integer extractKeyHash(final String key) {
        Assert.hasLength(key, "key cannot be empty or null");
        return key.hashCode();
    }
}
