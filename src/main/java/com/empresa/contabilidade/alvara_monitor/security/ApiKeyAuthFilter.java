package com.empresa.contabilidade.alvara_monitor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.task.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/tasks")) {
            final var requestApiKey = request.getHeader("X-API-KEY");
            if (apiKey.equals(requestApiKey)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("API Key inválida ou ausente.");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}