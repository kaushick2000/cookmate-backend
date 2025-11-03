package com.cookmate.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider provider = AuthProvider.LOCAL;
    
    @Column(name = "provider_id", length = 100)
    private String providerId;
    
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
    
    @Column(name = "is_locked")
    private Boolean isLocked = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorite> favorites = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MealPlan> mealPlans = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingList> shoppingLists = new HashSet<>();
    
    public enum Role {
        USER, ADMIN
    }
    
    public enum AuthProvider {
        LOCAL, GOOGLE, FACEBOOK
    }
}