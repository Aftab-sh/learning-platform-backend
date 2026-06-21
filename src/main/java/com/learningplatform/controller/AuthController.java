package com.learningplatform.controller;
import com.learningplatform.dto.ForgotPasswordRequest;
import com.learningplatform.dto.LoginRequest;
import com.learningplatform.dto.LoginResponse;
import com.learningplatform.dto.RegisterRequest;
import com.learningplatform.dto.ResetPasswordRequest;
import com.learningplatform.dto.UserResponse;
import com.learningplatform.entity.Role;
import com.learningplatform.entity.User;
import com.learningplatform.service.UserService;
import com.learningplatform.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/users")
@Slf4j
public class AuthController {

    
    private final UserService userService;

    
    private final AuthenticationManager authenticationManager;

    
    private final JwtUtil jwtUtil;
    
    @Autowired
    AuthController(UserService userService,
    		AuthenticationManager authenticationManager,
    		JwtUtil jwtUtil)
    {
    	this.userService=userService;
    	this.authenticationManager=authenticationManager;
    	this.jwtUtil=jwtUtil;
    	
    }

    // ── REGISTER ──
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterRequest request) 
    {

        request.setRole(Role.STUDENT);

        UserResponse userResponse =
                userService.registerUser(request);

        return ResponseEntity.ok(userResponse);
    }

    // ── CREATE TEACHER ──
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/teacher/create")
    public ResponseEntity<?> registerByAdmin(
            @Valid @RequestBody RegisterRequest request)
    {

        request.setRole(Role.TEACHER);

        UserResponse userResponse =
                userService.registerUser(request);

        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        try {
            // ✅ Step 1 — Authenticate (checks password via AuthenticationManager)
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            // ✅ Step 2 — Fetch user details
            User user = userService.findByEmail(request.getEmail());

            // ✅ Step 3 — Check email verification
            if (!user.isEmailVerified()) {
                log.warn("Login blocked — email not verified: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Email not verified. Please verify your email first."));
            }

            // ✅ Step 4 — Generate JWT
            String token = jwtUtil.generateToken(user.getEmail());

            log.info("Login successful for: {}", user.getEmail());

            // ✅ Step 5 — Return full response with name
            return ResponseEntity.ok(
                new LoginResponse(
                    user.getId(),
                    token,
                    user.getEmail(),
                    user.getRole().name(),
                    user.getName()
                )
            );

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));

        } catch (Exception e) {
            log.error("Login failed for email: {} | reason: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("email", user.getEmail());
        map.put("name", user.getName());
        map.put("role", user.getRole().name());
        return ResponseEntity.ok(map);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request)
    {
        return ResponseEntity.ok(
                userService.forgotPassword(
                        request.getEmail()));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request)
    {
        return ResponseEntity.ok(
                userService.resetPassword(
                        request.getToken(),
                        request.getNewPassword()));
    }
    
    
}