package com.cookmate.backend.service;

import com.cookmate.backend.dto.*;
import com.cookmate.backend.entity.PasswordResetToken;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.entity.UserPreferences;
import com.cookmate.backend.exception.BadRequestException;
import com.cookmate.backend.repository.PasswordResetTokenRepository;
import com.cookmate.backend.repository.UserRepository;
import com.cookmate.backend.repository.UserPreferencesRepository;
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
    private UserPreferencesRepository userPreferencesRepository;
    
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
        // First check if user exists
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElse(null);
        
        if (user == null) {
            throw new BadRequestException("User does not exist. Please try signing up.");
        }
        
        try {
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
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new BadRequestException("Invalid password. Please check your credentials.");
        } catch (Exception e) {
            throw new BadRequestException("Authentication failed. Please try again.");
        }
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
     * Request password reset with token
     */
    @Transactional
    public ApiResponse requestPasswordReset(PasswordResetRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found with this email address"));
        
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser_Id(user.getId());
        
        // Generate reset token
        String token = UUID.randomUUID().toString();
        
        // Create new reset token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        
        // Save token to database
        tokenRepository.save(resetToken);
        
        // Try to send reset link via email
        try {
            emailService.sendPasswordResetLink(user.getEmail(), token);
            return new ApiResponse(true, "Password reset link sent to your email");
        } catch (Exception e) {
            // Log the error but don't fail the request
            System.err.println("Failed to send email: " + e.getMessage());
            // For development: return the token so it can be used
            return new ApiResponse(true, "Password reset token generated. Token: " + token + " (Email service not configured)");
        }
    }
    
    /**
     * Reset password using token (updated to work with current schema)
     */
    @Transactional
    public ApiResponse resetPasswordWithToken(String token, String newPassword) {
        // Find valid token
        PasswordResetToken resetToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));
        
        // Check if token is expired
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Password reset token has expired. Please request a new one.");
        }
        
        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Delete the token (it's been used)
        tokenRepository.delete(resetToken);
        
        return new ApiResponse(true, "Password has been reset successfully");
    }
    
    /**
     * Reset password using token (deprecated - keeping for compatibility)
     */
    @Deprecated
    @Transactional
    public ApiResponse resetPassword(PasswordResetConfirm request) {
        throw new BadRequestException("This method is deprecated. Please use the verification code method instead.");
    }
    
    /**
     * Get current user by username
     */
    public User getCurrentUser(String username) {
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * Update user profile
     */
    @Transactional
    public User updateProfile(String username, UpdateProfileRequest request) {
        User user = getCurrentUser(username);
        
        // Update basic profile information
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            // Check if new username is already taken
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BadRequestException("Username is already taken");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        
        // Update preferences
        if (request.getPreferences() != null) {
            UserPreferences preferences = userPreferencesRepository.findByUser(user)
                    .orElse(new UserPreferences(user));
            
            UpdateProfileRequest.UserPreferences reqPrefs = request.getPreferences();
            
            // Map the preferences from DTO to Entity
            if (reqPrefs.getCookingSkillLevel() != null) {
                try {
                    preferences.setCookingSkillLevel(
                        UserPreferences.CookingSkillLevel.valueOf(reqPrefs.getCookingSkillLevel().toUpperCase())
                    );
                } catch (IllegalArgumentException e) {
                    // If invalid value, ignore or set to default
                }
            }
            
            if (reqPrefs.getDifficultyLevel() != null) {
                // Map difficulty level to cooking skill level if needed
                String diffLevel = reqPrefs.getDifficultyLevel().toLowerCase();
                if (diffLevel.equals("easy")) {
                    preferences.setCookingSkillLevel(UserPreferences.CookingSkillLevel.BEGINNER);
                } else if (diffLevel.equals("medium")) {
                    preferences.setCookingSkillLevel(UserPreferences.CookingSkillLevel.INTERMEDIATE);
                } else if (diffLevel.equals("hard")) {
                    preferences.setCookingSkillLevel(UserPreferences.CookingSkillLevel.ADVANCED);
                }
            }
            
            if (reqPrefs.getPreferredPrepTime() != null) {
                preferences.setPreferredPrepTime(reqPrefs.getPreferredPrepTime());
            }
            
            if (reqPrefs.getPreferredCookTime() != null) {
                preferences.setPreferredCookTime(reqPrefs.getPreferredCookTime());
            }
            
            if (reqPrefs.getHouseholdSize() != null) {
                preferences.setHouseholdSize(reqPrefs.getHouseholdSize());
            }
            
            if (reqPrefs.getBudgetPreference() != null) {
                try {
                    preferences.setBudgetPreference(
                        UserPreferences.BudgetPreference.valueOf(reqPrefs.getBudgetPreference().toUpperCase())
                    );
                } catch (IllegalArgumentException e) {
                    // If invalid value, ignore
                }
            }
            
            if (reqPrefs.getHealthGoals() != null) {
                preferences.setHealthGoals(reqPrefs.getHealthGoals());
            }
            
            if (reqPrefs.getFoodAllergies() != null) {
                preferences.setFoodAllergies(reqPrefs.getFoodAllergies());
            }
            
            // Store dietary restrictions as comma-separated string in food_allergies for now
            if (reqPrefs.getDietaryRestrictions() != null && !reqPrefs.getDietaryRestrictions().isEmpty()) {
                String dietary = String.join(", ", reqPrefs.getDietaryRestrictions());
                String currentAllergies = preferences.getFoodAllergies();
                if (currentAllergies != null && !currentAllergies.isEmpty()) {
                    preferences.setFoodAllergies(currentAllergies + "; Dietary: " + dietary);
                } else {
                    preferences.setFoodAllergies("Dietary: " + dietary);
                }
            }
            
            if (reqPrefs.getCookingEquipment() != null) {
                preferences.setCookingEquipment(reqPrefs.getCookingEquipment());
            }
            
            if (reqPrefs.getMealPlanningFrequency() != null) {
                try {
                    preferences.setMealPlanningFrequency(
                        UserPreferences.MealPlanningFrequency.valueOf(reqPrefs.getMealPlanningFrequency().toUpperCase())
                    );
                } catch (IllegalArgumentException e) {
                    // If invalid value, ignore
                }
            }
            
            // Store cuisine preferences as comma-separated string
            if (reqPrefs.getCuisinePreferences() != null && !reqPrefs.getCuisinePreferences().isEmpty()) {
                preferences.setCuisinePreferences(String.join(", ", reqPrefs.getCuisinePreferences()));
            }
            
            // Store meal types as comma-separated string
            if (reqPrefs.getMealTypes() != null && !reqPrefs.getMealTypes().isEmpty()) {
                preferences.setMealTypes(String.join(", ", reqPrefs.getMealTypes()));
            }
            
            userPreferencesRepository.save(preferences);
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Cleanup expired tokens (can be scheduled)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}