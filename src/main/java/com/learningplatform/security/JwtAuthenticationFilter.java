package com.learningplatform.security;

import com.learningplatform.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication
        .UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context
        .SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication
        .WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            CustomUserDetailsService customUserDetailsService) 
    {

        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException 
    {

    	  // ✅ OPTIONS bypass (preflight)
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        // ✅ Current request path
        String path = request.getServletPath();

        // ✅ Public endpoints bypass
        if (path.equals("/api/users/login")
                || path.equals("/api/users/register")) {

            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Authorization header
        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        // ✅ Check Bearer token
        if (authHeader != null
                && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            try {

                // ✅ Extract email from token
                email = jwtUtil.extractUserEmail(token);

            } catch (Exception e) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid JWT Token");

                return;
            }
        }

        // ✅ If email exists and user not authenticated
        if (email != null
                && SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            // ✅ Load user from database
            UserDetails userDetails =
                    customUserDetailsService
                            .loadUserByUsername(email);

            // ✅ Validate token
            if (jwtUtil.validateToken(
                    email,
                    userDetails,
                    token)) {

                // ✅ Create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                // ✅ Set authentication in security context
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        // ✅ Continue request
        filterChain.doFilter(request, response);
    }
}