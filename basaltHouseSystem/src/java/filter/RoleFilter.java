package filter;

import dto.UserLoginDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows protected routes to be used only by their corresponding account role.
 */
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        int requiredRole = AccessRules.requiredRole(
                AuthenticationFilter.getApplicationPath(httpRequest),
                httpRequest.getMethod(),
                httpRequest.getParameter("action"));

        if (requiredRole == AccessRules.PUBLIC) {
            chain.doFilter(request, response);
            return;
        }

        UserLoginDTO user = AuthenticationFilter.getCurrentUser(httpRequest);
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (user.getRoleId() != requiredRole) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }
}
