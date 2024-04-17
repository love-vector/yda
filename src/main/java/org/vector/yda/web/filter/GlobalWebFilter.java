package org.vector.yda.web.filter;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import org.vector.yda.exception.unauthorized.UserUnauthorizedException;
import org.vector.yda.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalWebFilter implements Filter {

    private final UserService userService;

    @Override
    public void doFilter(
            final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws ServletException, IOException {
        var request = getFormattedRequest((HttpServletRequest) servletRequest);
        var stopWatch = new StopWatch();
        var userEmail = getCurrentUserEmail();
        logExecutionStart(userEmail, request, stopWatch);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception exception) {
            logExecutionFail(userEmail, request, stopWatch, exception);
            throw exception;
        }
        logExecutionFinish(userEmail, request, stopWatch);
    }

    private String getFormattedRequest(final HttpServletRequest request) {
        return request.getMethod() + StringUtils.SPACE + request.getRequestURL().toString();
    }

    private String getCurrentUserEmail() {
        try {
            return userService.getCurrentUser().email();
        } catch (UserUnauthorizedException userUnauthorizedException) {
            return "No User Authorized";
        }
    }

    private void logExecutionStart(final String userEmail, final String request, final StopWatch stopWatch) {
        stopWatch.start();
        log.debug(
                """
                        Starting request execution:
                        User - {}
                        Request - {}
                        """,
                userEmail,
                request);
    }

    private void logExecutionFinish(final String userEmail, final String request, final StopWatch stopWatch) {
        stopWatch.stop();
        log.debug(
                """
                        Finished request execution:
                        User - {}
                        Request - {}
                        Execution time (seconds) - {}
                        """,
                userEmail,
                request,
                stopWatch.getTotalTimeSeconds());
    }

    private void logExecutionFail(
            final String userEmail, final String request, final StopWatch stopWatch, final Throwable exception) {
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
                request,
                stopWatch.getTotalTimeSeconds(),
                exception.getMessage());
    }
}
