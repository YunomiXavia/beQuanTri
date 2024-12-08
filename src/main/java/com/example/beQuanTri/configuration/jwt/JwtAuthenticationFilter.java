package com.example.beQuanTri.configuration.jwt;

import com.example.beQuanTri.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        try {
            if (jwtTokenUtil.validateToken(token)) {
                String username = jwtTokenUtil.getUsernameFromToken(token);

                String role = jwtTokenUtil.getRoleFromToken(token);
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                UserAuthenticationToken authentication =
                        new UserAuthenticationToken(username, null, authorities);

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            } else {
                response.setStatus(ErrorCode.INVALID_TOKEN.getStatusCode().value());
                response.getWriter().write(ErrorCode.INVALID_TOKEN.getMessage());
                return;
            }
        } catch (ParseException e) {
            response.setStatus(ErrorCode.CANNOT_PARSING_TOKEN.getStatusCode().value());
            response.getWriter().write(ErrorCode.CANNOT_PARSING_TOKEN.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }
}
