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
package ai.yda.framework.channel.rest.spring.streaming.session;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;

/**
 * Detects if a {@link WebSession} for the current {@link ServerWebExchange} has been started and starts one if it's
 * not.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class SessionHandlerFilter implements WebFilter {

    /**
     * Default constructor for {@link SessionHandlerFilter}.
     */
    public SessionHandlerFilter() {}

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Mono<Void> filter(@NonNull final ServerWebExchange exchange, @NonNull final WebFilterChain chain) {
        return exchange.getSession().flatMap(webSession -> {
            if (!webSession.isStarted()) {
                webSession.start();
            }
            return chain.filter(exchange).contextWrite(Context.of(WebSession.class, webSession));
        });
    }
}
