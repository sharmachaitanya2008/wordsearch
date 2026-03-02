/*
package com.example.wordsearch.security;

import com.example.wordsearch.model.User;
import com.example.wordsearch.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

@Component
@RequiredArgsConstructor
public class PasswordChangeEnforcementFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName())) {

            User user = userRepository.findByUsername(auth.getName()).orElse(null);

            if (user != null && user.isMustChangePassword()) {

                String path = request.getRequestURI();

                boolean allowed =
                        path.startsWith("/api/auth/change-password") ||
                                path.startsWith("/api/auth/logout");

                if (!allowed) {
                    response.setStatus(SC_FORBIDDEN);
                    response.getWriter().write(
                            "{\"error\":\"Password change required\"}"
                    );
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}*/
