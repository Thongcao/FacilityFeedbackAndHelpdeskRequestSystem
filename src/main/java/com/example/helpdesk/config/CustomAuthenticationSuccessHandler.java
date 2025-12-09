package com.example.helpdesk.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Custom authentication success handler that redirects users based on their role
 * and the login page they used.
 * 
 * @author Facility Helpdesk Team
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String referer = request.getHeader("Referer");
        String loginType = request.getParameter("loginType");
        
        // Check user role
        boolean isStudent = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isStaff = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));
        
        // Check if login came from admin login page
        // Check by hidden field first, then by referer
        boolean fromAdminLogin = "admin".equals(loginType) ||
                                 (referer != null && referer.contains("/admin/login"));
        
        // If login from /admin/login
        if (fromAdminLogin) {
            if (isAdmin || isStaff) {
                // Both Admin and Staff go to admin dashboard
                response.sendRedirect("/admin/dashboard");
            } else {
                // STUDENT trying to login as admin - invalidate session and redirect with error
                request.getSession().invalidate();
                response.sendRedirect("/admin/login?roleError=true");
            }
        } 
        // If login from /login (user login)
        else {
            if (isStudent) {
                response.sendRedirect("/tickets/new");
            } else {
                // ADMIN/STAFF trying to login as user - invalidate session and redirect with error
                request.getSession().invalidate();
                // Redirect to appropriate login page based on role
                if (isAdmin || isStaff) {
                    response.sendRedirect("/admin/login?roleError=true");
                } else {
                    response.sendRedirect("/login?roleError=true");
                }
            }
        }
    }
}


