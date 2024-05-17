package ai.yda.shared.logging;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingWebFilter implements Filter {

    @Override
    public void doFilter(
            final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
            throws ServletException, IOException {
        var request = getFormattedRequest((HttpServletRequest) servletRequest);
        var stopWatch = new StopWatch();
        logExecutionStart(request, stopWatch);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception exception) {
            logExecutionFail(request, stopWatch, exception);
            throw exception;
        }
        logExecutionFinish(request, stopWatch);
    }

    private String getFormattedRequest(final HttpServletRequest request) {
        return request.getMethod() + StringUtils.SPACE + request.getRequestURL().toString();
    }

    private void logExecutionStart(final String request, final StopWatch stopWatch) {
        stopWatch.start();
        log.debug(
                """
                        Starting request execution:
                        Request - {}
                        """,
                request);
    }

    private void logExecutionFinish(final String request, final StopWatch stopWatch) {
        stopWatch.stop();
        log.debug(
                """
                        Finished request execution:
                        Request - {}
                        Execution time (seconds) - {}
                        """,
                request,
                stopWatch.getTotalTimeSeconds());
    }

    private void logExecutionFail(final String request, final StopWatch stopWatch, final Throwable exception) {
        stopWatch.stop();
        log.error(
                """
                        Failed request execution:
                        Request - {}
                        Execution time (seconds) - {}
                        Message - {}
                        """,
                request,
                stopWatch.getTotalTimeSeconds(),
                exception.getMessage());
    }
}
