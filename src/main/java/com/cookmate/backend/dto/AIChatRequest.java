package com.cookmate.backend.dto;

import java.util.List;

public class AIChatRequest {
    private String message;
    private List<String> ingredients;
    private List<String> dietaryRestrictions;
    private String mealType;
    private String chatType; // "recipe_suggestion" or "general_chat"

    public AIChatRequest() {}

    public AIChatRequest(String message, List<String> ingredients, List<String> dietaryRestrictions, String mealType, String chatType) {
        this.message = message;
        this.ingredients = ingredients;
        this.dietaryRestrictions = dietaryRestrictions;
        this.mealType = mealType;
        this.chatType = chatType;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(List<String> dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getChatType() { return chatType; }
    public void setChatType(String chatType) { this.chatType = chatType; }
}