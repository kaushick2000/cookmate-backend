package com.cookmate.backend.service;

import com.cookmate.backend.dto.*;
import com.cookmate.backend.entity.PasswordResetToken;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.exception.BadRequestException;
import com.cookmate.backend.exception.ResourceNotFoundException;
import com.cookmate.backend.repository.PasswordResetTokenRepository;
import com.cookmate.backend.repository.UserRepository;
import com.cookmate.backend.security.jwt.JwtUtils;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Authenticate user and generate JWT token
     */
    @Transactional
    public AuthResponse login(AuthRequest request) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
        
        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Extract role (remove ROLE_ prefix)
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
        
        // Return authentication response
        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role
        );
    }
    
    /**
     * Register a new user
     */
    @Transactional
    public ApiResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(User.Role.USER);
        user.setProvider(User.AuthProvider.LOCAL);
        user.setIsEnabled(true);
        user.setIsLocked(false);
        
        // Save user to database
        userRepository.save(user);
        
        return new ApiResponse(true, "User registered successfully");
    }
    
    /**
     * Request password reset - sends email with reset link
     */
    @Transactional
    public ApiResponse requestPasswordReset(PasswordResetRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser_Id(user.getId());
        
        // Create new reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours
        
        // Save token to database
        tokenRepository.save(resetToken);
        
        // Send password reset email
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
        
        return new ApiResponse(true, "Password reset link sent to your email");
    }
    
    /**
     * Reset password using token
     */
    @Transactional
    public ApiResponse resetPassword(PasswordResetConfirm request) {
        // Find token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid password reset token"));
        
        // Check if token has expired
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Password reset token has expired");
        }
        
        // Get user and update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Delete the used token
        tokenRepository.delete(resetToken);
        
        return new ApiResponse(true, "Password reset successfully");
    }
    
    /**
     * Cleanup expired tokens (can be scheduled)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}