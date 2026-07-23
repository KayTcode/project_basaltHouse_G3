package filter;

import dto.UserLoginDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import services.AuthService;

/**
 * Requires a valid login session only for protected application routes.
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = getApplicationPath(httpRequest);

        if (!AccessRules.requiresAuthentication(
                path, httpRequest.getMethod(), httpRequest.getParameter("action"))) {
            chain.doFilter(request, response);
            return;
        }

        if (getCurrentUser(httpRequest) == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    static String getApplicationPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        if (contextPath == null || contextPath.isEmpty()) {
            return requestUri;
        }
        if (requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }

    static UserLoginDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object currentUser = session.getAttribute(AuthService.USER_SESSION_KEY);
        if (currentUser instanceof UserLoginDTO) {
            return (UserLoginDTO) currentUser;
        }
        return null;
    }
}
