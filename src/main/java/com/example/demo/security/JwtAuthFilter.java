package com.example.demo.security;

import com.example.demo.service.TokenService;
import com.example.demo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    public JwtAuthFilter(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (!tokenService.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = tokenService.getUsername(token);
        var userDetails = userService.findByUsername(username);

        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                List.of()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final UserDetailsService userDetailsService;
//
//    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService uds) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = uds;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain chain)
//            throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//        if (header != null && header.startsWith("Bearer ")) {
//            String token = header.substring(7);
//
//            if (jwtUtil.isValid(token)) {
//                String username = jwtUtil.extractUsername(token);
//                if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                    var userDetails = userDetailsService.loadUserByUsername(username);
//                    var auth = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            }
//        }
//        chain.doFilter(request, response);
//    }
//}
