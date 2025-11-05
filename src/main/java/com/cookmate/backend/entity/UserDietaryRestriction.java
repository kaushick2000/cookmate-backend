package com.cookmate.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_dietary_restrictions")
public class UserDietaryRestriction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dietary_restriction_id", nullable = false)
    private DietaryRestriction dietaryRestriction;
    
    @Column(name = "strictness_level")
    @Enumerated(EnumType.STRING)
    private StrictnessLevel strictnessLevel = StrictnessLevel.MODERATE;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Enum for strictness level
    public enum StrictnessLevel {
        FLEXIBLE, MODERATE, STRICT, VERY_STRICT
    }
    
    // Constructors
    public UserDietaryRestriction() {}
    
    public UserDietaryRestriction(User user, DietaryRestriction dietaryRestriction) {
        this.user = user;
        this.dietaryRestriction = dietaryRestriction;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public DietaryRestriction getDietaryRestriction() {
        return dietaryRestriction;
    }
    
    public void setDietaryRestriction(DietaryRestriction dietaryRestriction) {
        this.dietaryRestriction = dietaryRestriction;
    }
    
    public StrictnessLevel getStrictnessLevel() {
        return strictnessLevel;
    }
    
    public void setStrictnessLevel(StrictnessLevel strictnessLevel) {
        this.strictnessLevel = strictnessLevel;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}