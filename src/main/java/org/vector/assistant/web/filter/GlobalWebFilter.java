package org.vector.assistant.web.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import org.vector.assistant.model.dto.UserDto;
import org.vector.assistant.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalWebFilter implements WebFilter {

    private static final String NO_USER_MESSAGE = "No User Authorized";

    private final UserService userService;

    @NotNull
    @Override
    public Mono<Void> filter(
            @NotNull final ServerWebExchange serverWebExchange, @NotNull final WebFilterChain webFilterChain) {
        return userService
                .getCurrentUser()
                .map(UserDto::email)
                .defaultIfEmpty(NO_USER_MESSAGE)
                .flatMap(userEmail -> processChain(webFilterChain, serverWebExchange, userEmail));
    }

    private Mono<Void> processChain(
            @NotNull final WebFilterChain webFilterChain,
            @NotNull final ServerWebExchange serverWebExchange,
            final String userEmail) {
        var formattedRequest = getFormattedRequest(serverWebExchange);
        var stopWatch = new StopWatch();
        return webFilterChain
                .filter(serverWebExchange)
                .doOnSubscribe(subscription -> logExecutionStart(formattedRequest, userEmail, stopWatch))
                .doOnSuccess(success -> logExecutionFinish(formattedRequest, userEmail, stopWatch))
                .doOnCancel(() -> logExecutionCancel(formattedRequest, userEmail, stopWatch))
                .doOnError(error -> logExecutionFail(formattedRequest, userEmail, stopWatch, error));
    }

    private String getFormattedRequest(final ServerWebExchange serverWebExchange) {
        return String.format(
                "Request: %s %s",
                serverWebExchange.getRequest().getMethod(),
                serverWebExchange.getRequest().getURI());
    }

    private void logExecutionStart(final String userEmail, final String formattedRequest, final StopWatch stopWatch) {
        stopWatch.start();
        log.debug(
                """
                        Starting request execution:
                        User - {}
                        Request - {}
                        """,
                userEmail,
                formattedRequest);
    }

    private void logExecutionFinish(final String userEmail, final String formattedRequest, final StopWatch stopWatch) {
        stopWatch.stop();
        log.debug(
                """
                        Finished request execution:
                        User - {}
                        Request - {}
                        Execution time (seconds) - {}
                        """,
                userEmail,
                formattedRequest,
                stopWatch.getTotalTimeSeconds());
    }

    private void logExecutionCancel(final String userEmail, final String formattedRequest, final StopWatch stopWatch) {
        stopWatch.stop();
        log.debug(
                """
                        Canceled request execution:
                        User - {}
                        Request - {}
                        Execution time (seconds) - {}
                        """,
                userEmail,
                formattedRequest,
                stopWatch.getTotalTimeSeconds());
    }

    private void logExecutionFail(
            final String userEmail,
            final String formattedRequest,
            final StopWatch stopWatch,
            final Throwable exception) {
        stopWatch.stop();
        log.error(
                """
                        Failed request execution:
                        User - {}
                        Request - {}
                        Execution time (seconds) - {}
                        Message - {}
                        """,
                userEmail,
                formattedRequest,
                stopWatch.getTotalTimeSeconds(),
                exception.getMessage());
    }
}
