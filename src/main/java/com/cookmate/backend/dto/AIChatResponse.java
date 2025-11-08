package com.cookmate.backend.dto;

import java.util.List;

public class AIChatResponse {
    private String message;
    private List<RecipeSuggestion> recipeSuggestions;
    private String status; // "success", "error", "fallback"

    public AIChatResponse() {}

    public AIChatResponse(String message, List<RecipeSuggestion> recipeSuggestions, String status) {
        this.message = message;
        this.recipeSuggestions = recipeSuggestions;
        this.status = status;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<RecipeSuggestion> getRecipeSuggestions() { return recipeSuggestions; }
    public void setRecipeSuggestions(List<RecipeSuggestion> recipeSuggestions) { this.recipeSuggestions = recipeSuggestions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Inner class for recipe suggestions
    public static class RecipeSuggestion {
        private String name;
        private String description;
        private String cookTime;
        private String difficulty;

        public RecipeSuggestion() {}

        public RecipeSuggestion(String name, String description, String cookTime, String difficulty) {
            this.name = name;
            this.description = description;
            this.cookTime = cookTime;
            this.difficulty = difficulty;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getCookTime() { return cookTime; }
        public void setCookTime(String cookTime) { this.cookTime = cookTime; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    }
}