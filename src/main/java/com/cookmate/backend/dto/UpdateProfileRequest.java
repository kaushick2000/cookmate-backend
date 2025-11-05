package com.cookmate.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UpdateProfileRequest {
    
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    private UserPreferences preferences;
    
    // Constructors
    public UpdateProfileRequest() {}
    
    public UpdateProfileRequest(String firstName, String lastName, String username, String imageUrl, UserPreferences preferences) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.imageUrl = imageUrl;
        this.preferences = preferences;
    }
    
    // Getters and setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public UserPreferences getPreferences() {
        return preferences;
    }
    
    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }
    
    public static class UserPreferences {
        private List<String> dietaryRestrictions;
        private List<String> cuisinePreferences;
        private String difficultyLevel;
        private List<String> mealTypes;
        private String cookingSkillLevel;
        private Integer preferredPrepTime;
        private Integer preferredCookTime;
        private Integer householdSize;
        private String budgetPreference;
        private String healthGoals;
        private String foodAllergies;
        private String cookingEquipment;
        private String mealPlanningFrequency;
        
        // Constructors
        public UserPreferences() {}
        
        public UserPreferences(List<String> dietaryRestrictions, List<String> cuisinePreferences, 
                             String difficultyLevel, List<String> mealTypes) {
            this.dietaryRestrictions = dietaryRestrictions;
            this.cuisinePreferences = cuisinePreferences;
            this.difficultyLevel = difficultyLevel;
            this.mealTypes = mealTypes;
        }
        
        // Getters and setters
        public List<String> getDietaryRestrictions() {
            return dietaryRestrictions;
        }
        
        public void setDietaryRestrictions(List<String> dietaryRestrictions) {
            this.dietaryRestrictions = dietaryRestrictions;
        }
        
        public List<String> getCuisinePreferences() {
            return cuisinePreferences;
        }
        
        public void setCuisinePreferences(List<String> cuisinePreferences) {
            this.cuisinePreferences = cuisinePreferences;
        }
        
        public String getDifficultyLevel() {
            return difficultyLevel;
        }
        
        public void setDifficultyLevel(String difficultyLevel) {
            this.difficultyLevel = difficultyLevel;
        }
        
        public List<String> getMealTypes() {
            return mealTypes;
        }
        
        public void setMealTypes(List<String> mealTypes) {
            this.mealTypes = mealTypes;
        }

        public String getCookingSkillLevel() {
            return cookingSkillLevel;
        }

        public void setCookingSkillLevel(String cookingSkillLevel) {
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

        public String getBudgetPreference() {
            return budgetPreference;
        }

        public void setBudgetPreference(String budgetPreference) {
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

        public String getMealPlanningFrequency() {
            return mealPlanningFrequency;
        }

        public void setMealPlanningFrequency(String mealPlanningFrequency) {
            this.mealPlanningFrequency = mealPlanningFrequency;
        }
    }
}