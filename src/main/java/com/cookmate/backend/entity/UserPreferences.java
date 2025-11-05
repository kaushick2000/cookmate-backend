package com.cookmate.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "cooking_skill_level")
    @Enumerated(EnumType.STRING)
    private CookingSkillLevel cookingSkillLevel;
    
    @Column(name = "preferred_prep_time")
    private Integer preferredPrepTime; // in minutes
    
    @Column(name = "preferred_cook_time")
    private Integer preferredCookTime; // in minutes
    
    @Column(name = "household_size")
    private Integer householdSize;
    
    @Column(name = "budget_preference")
    @Enumerated(EnumType.STRING)
    private BudgetPreference budgetPreference;
    
    @Column(name = "health_goals", length = 1000)
    private String healthGoals;
    
    @Column(name = "food_allergies", length = 1000)
    private String foodAllergies;
    
    @Column(name = "cooking_equipment", length = 1000)
    private String cookingEquipment;
    
    @Column(name = "meal_planning_frequency")
    @Enumerated(EnumType.STRING)
    private MealPlanningFrequency mealPlanningFrequency;
    
    @Column(name = "cuisine_preferences", length = 1000)
    private String cuisinePreferences;
    
    @Column(name = "meal_types", length = 500)
    private String mealTypes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum CookingSkillLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }
    
    public enum BudgetPreference {
        LOW, MODERATE, HIGH, NO_PREFERENCE
    }
    
    public enum MealPlanningFrequency {
        DAILY, WEEKLY, MONTHLY, RARELY, NEVER
    }
    
    // Constructors
    public UserPreferences() {}
    
    public UserPreferences(User user) {
        this.user = user;
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
    
    public CookingSkillLevel getCookingSkillLevel() {
        return cookingSkillLevel;
    }
    
    public void setCookingSkillLevel(CookingSkillLevel cookingSkillLevel) {
        this.cookingSkillLevel = cookingSkillLevel;
    }
    
    public Integer getPreferredPrepTime() {
        return preferredPrepTime;
    }
    
    public void setPreferredPrepTime(Integer preferredPrepTime) {
        this.preferredPrepTime = preferredPrepTime;
    }
    
    public Integer getPreferredCookTime() {
        return preferredCookTime;
    }
    
    public void setPreferredCookTime(Integer preferredCookTime) {
        this.preferredCookTime = preferredCookTime;
    }
    
    public Integer getHouseholdSize() {
        return householdSize;
    }
    
    public void setHouseholdSize(Integer householdSize) {
        this.householdSize = householdSize;
    }
    
    public BudgetPreference getBudgetPreference() {
        return budgetPreference;
    }
    
    public void setBudgetPreference(BudgetPreference budgetPreference) {
        this.budgetPreference = budgetPreference;
    }
    
    public String getHealthGoals() {
        return healthGoals;
    }
    
    public void setHealthGoals(String healthGoals) {
        this.healthGoals = healthGoals;
    }
    
    public String getFoodAllergies() {
        return foodAllergies;
    }
    
    public void setFoodAllergies(String foodAllergies) {
        this.foodAllergies = foodAllergies;
    }
    
    public String getCookingEquipment() {
        return cookingEquipment;
    }
    
    public void setCookingEquipment(String cookingEquipment) {
        this.cookingEquipment = cookingEquipment;
    }
    
    public MealPlanningFrequency getMealPlanningFrequency() {
        return mealPlanningFrequency;
    }
    
    public void setMealPlanningFrequency(MealPlanningFrequency mealPlanningFrequency) {
        this.mealPlanningFrequency = mealPlanningFrequency;
    }
    
    public String getCuisinePreferences() {
        return cuisinePreferences;
    }
    
    public void setCuisinePreferences(String cuisinePreferences) {
        this.cuisinePreferences = cuisinePreferences;
    }
    
    public String getMealTypes() {
        return mealTypes;
    }
    
    public void setMealTypes(String mealTypes) {
        this.mealTypes = mealTypes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}