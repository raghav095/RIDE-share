package org.example.rideshare.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.rideshare.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String tokenUsername = null;
        String tokenValue = null;
        String tokenRole = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            tokenValue = authorizationHeader.substring(7);
            try {
                tokenUsername = jwtUtil.extractUsername(tokenValue);
                tokenRole = jwtUtil.extractRole(tokenValue);
            } catch (Exception e) {
                // token could not be parsed; let the filter chain continue
            }
        }

        if (tokenUsername != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(tokenValue, tokenUsername)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        tokenUsername, null, Collections.singletonList(new SimpleGrantedAuthority(tokenRole)));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
