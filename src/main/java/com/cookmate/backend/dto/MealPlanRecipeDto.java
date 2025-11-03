package com.cookmate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanRecipeDto {
    
    private Long id;
    private Long recipeId;
    private String recipeTitle;
    private String recipeImageUrl;
    private LocalDate plannedDate;
    private String mealTime;
    private Integer servings;
}