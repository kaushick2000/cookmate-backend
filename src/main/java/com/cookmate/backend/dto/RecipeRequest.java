package com.cookmate.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String cuisineType;
    private String mealType;
    private String difficultyLevel;
    private Integer prepTime;
    private Integer cookTime;
    
    @NotNull(message = "Servings is required")
    private Integer servings;
    
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
    private BigDecimal fiber;
    private String imageUrl;
    private String videoUrl;
    private byte[] imageData;
    private String imageFilename;
    private String imageContentType;
    private Boolean isVegetarian = false;
    private Boolean isVegan = false;
    private Boolean isGlutenFree = false;
    private Boolean isDairyFree = false;
    
    private List<RecipeIngredientRequest> ingredients;
    private List<InstructionRequest> instructions;
}