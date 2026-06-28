package com.learningplatform.service;

import com.learningplatform.Exception.BadRequestException;
import com.learningplatform.Exception.ResourceNotFoundException;
import com.learningplatform.Exception.UnauthorizedException;
import com.learningplatform.dto.LoginRequest;
import com.learningplatform.dto.LoginResponse;
import com.learningplatform.dto.RegisterRequest;
import com.learningplatform.dto.UserResponse;
import com.learningplatform.entity.User;
import com.learningplatform.repository.UserRepository;
import com.learningplatform.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService 
{

    
    private final UserRepository userRepository;

    
    private final EmailService emailService;

    
    private final PasswordEncoder passwordEncoder;

    
    private final JwtUtil jwtUtil;
    
    @Autowired
    UserService(UserRepository userRepository,
    		EmailService emailService,
    		PasswordEncoder passwordEncoder,
    		JwtUtil jwtUtil)
    {
this.jwtUtil=jwtUtil;
this.passwordEncoder=passwordEncoder;
this.emailService=emailService;
this.userRepository=userRepository;
    	
    }

    // ── REGISTER ──
    public UserResponse registerUser(RegisterRequest request) 
    {
    	log.info("Registration request received");
    	
    	
        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());
        log.info("Checking existing user");


        if (existingUserOpt.isPresent()) 
        {
            User existingUser = existingUserOpt.get();
            if (existingUser.isEmailVerified())
            {
            	log.warn("User already exists");
                throw new BadRequestException("User already exists and verified");
            }
            // Resend: update token and expiry
            String newToken = UUID.randomUUID().toString();
            existingUser.setVerificationToken(newToken);
            existingUser.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(existingUser);
            
            emailService.sendVerificationEmail(existingUser, newToken);
            throw new BadRequestException("Verification email resent. Check your Inbox");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEmailVerified(true); // ✅ Auto verified

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        
        );
//        User user = new User();
//        user.setName(request.getName());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRole(request.getRole());
//        user.setEmailVerified(false);
//        
//        log.info("Generating verification token");
//        String verificationToken = UUID.randomUUID().toString();
//        user.setVerificationToken(verificationToken);
//        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1)); // ✅ expiry set
//        
//        log.info("Saving user into database");
//        User savedUser = userRepository.save(user);
//        
//        log.info("Sending verification email");
//        emailService.sendVerificationEmail(savedUser, verificationToken);
//
//        log.info("Registration completed successfully");
//        return new UserResponse(
//                savedUser.getId(),
//                savedUser.getName(),
//                savedUser.getEmail(),
//                savedUser.getRole().name()
//        );
        
        
    }

 // ── LOGIN ──
    public LoginResponse login(LoginRequest request)
    {
        log.info("Searching user with email : {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                new ResourceNotFoundException("User", "email", request.getEmail()));

        log.info("Login request received for email : {}", request.getEmail());

        if (!user.isEmailVerified())
        {
            log.warn("Email not verified for {}", request.getEmail());
            throw new UnauthorizedException("Email not verified. Please verify your email first.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        log.info("User logged in successfully : {}", user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());

        // ✅ Poora LoginResponse object return karo
        return new LoginResponse(
                user.getId(),
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getName()
        );
    }

    // ── Find by email ──
    public User findByEmail(String email)
    {
        return userRepository.findByEmail(email)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("User", "email", email));    }

    // ── Find by ID ──
    public User findById(Long id)
    {
    	
        return userRepository.findById(id)
        		.orElseThrow(() ->
        	    new ResourceNotFoundException("User", "id", id));    }
    
 // Add inside UserService class

    
    
    // this two method si for email verification 
    public String verifyEmailToken(String token) 
    {
    	log.info("Email verification started");
    	
    	
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));

        if (user.isEmailVerified()) 
        {
        	log.warn("Email already verified");
            throw new BadRequestException("Email already verified");
        }

        if (user.getVerificationTokenExpiry() == null || 
            user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
        	throw new BadRequestException(
        	        "Verification token has expired. Please request a new one.");        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        
        log.info("Email verified successfully");
        return "Email verified successfully! You can now login.";
    }

    
    public String resendVerificationEmail(String email) 
    {
    	log.info("Resend verification request");
        User user = userRepository.findByEmail(email)
        		.orElseThrow(() ->
        		
        	    new ResourceNotFoundException("User", "email", email));
        
        if (user.isEmailVerified())
        {
        	log.info("Email is already verified");
            throw new BadRequestException("Email is already verified");

        	
           // throw new RuntimeException("Email is already verified");
        }

        log.info("Generating new token");
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        log.info("Sending email");
        emailService.sendVerificationEmail(user, newToken);
        log.info("Verification email sent");

        return "Verification email resent successfully. Check your inbox.";
    }
    
    
    public String forgotPassword(String email)
    {
    	log.info("Forgot password request received");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "User",
                            "email",
                            email));

        log.info("Generating reset token");
        String resetToken = UUID.randomUUID().toString();

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(
                LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        emailService.sendForgotPasswordEmail(
                user.getEmail(),
                resetToken);

        return "Password reset link sent successfully.";
    }
    
    
    public String resetPassword(
            String token,
            String newPassword)
    {
    	log.info("Searching reset token");
        User user = userRepository
                .findByResetToken(token)
                .orElseThrow(() ->
                    new BadRequestException(
                            "Invalid reset token"));

        if(user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now()))
        {
        	log.warn("Token expired");
            throw new BadRequestException(
                    "Reset token expired");
        }

        log.info("Encoding password");
        user.setPassword(
        		
                passwordEncoder.encode(newPassword));

        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
        log.info("Password reset completed");

        return "Password reset successfully";
    }
}