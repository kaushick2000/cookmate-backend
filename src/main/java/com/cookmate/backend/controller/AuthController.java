package com.cookmate.backend.controller;

import com.cookmate.backend.dto.*;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.service.AuthService;
import com.cookmate.backend.dto.AuthRequest;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal; 
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request, HttpServletResponse servletResponse) {
        AuthResponse response = authService.login(request);

        // Set JWT as HttpOnly cookie so browsers will automatically send it on subsequent requests
        Cookie jwtCookie = new Cookie("JWT", response.getToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        // Set max age based on jwt.expiration (in milliseconds) if available via AuthService/JwtUtils; using 1 day fallback
        jwtCookie.setMaxAge(24 * 60 * 60);
        // For local development we won't set secure flag; in production setSecure(true) when using HTTPS
        servletResponse.addCookie(jwtCookie);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        ApiResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        ApiResponse response = authService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetConfirm request) {
        ApiResponse response = authService.resetPasswordWithToken(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request, 
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User updatedUser = authService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updatedUser);
    }
}