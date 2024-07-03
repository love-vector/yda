package ai.yda.common.shared.filter;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import ai.yda.common.shared.service.SessionProvider;

@RequiredArgsConstructor
public class HttpSessionThreadLocalFilter implements Filter {

    private final SessionProvider sessionProvider;

    /**
     * Filters incoming requests and sets the HttpSession in the SessionProvider.
     *
     * This method retrieves the HttpSession from the HttpServletRequest, sets it in the SessionProvider along with the session ID,
     * and then continues the filter chain.
     *
     * @param request  the incoming ServletRequest
     * @param response the outgoing ServletResponse
     * @param chain    the FilterChain to pass the request and response to the next filter or servlet
     * @throws IOException      if an I/O error occurs during filtering
     * @throws ServletException if a servlet error occurs during filtering
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            var httpSession = httpRequest.getSession();
            sessionProvider.setSessionId(httpSession.getId());
        }
        chain.doFilter(request, response);
    }
}
