package com.cookmate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    
    private Long id;
    private String title;
    private String description;
    private String cuisineType;
    private String mealType;
    private String difficultyLevel;
    private Integer prepTime;
    private Integer cookTime;
    private Integer totalTime;
    private Integer servings;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
    private BigDecimal fiber;
    private String imageUrl;
    private String videoUrl;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isDairyFree;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private Integer viewCount;
    private Long createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private List<RecipeIngredientDto> ingredients;
    private List<InstructionDto> instructions;
}