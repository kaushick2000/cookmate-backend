package com.cookmate.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanRecipeRequest {
    
    @NotNull(message = "Recipe ID is required")
    private Long recipeId;
    
    @NotNull(message = "Planned date is required")
    private LocalDate plannedDate;
    
    private String mealTime;
    
    @NotNull(message = "Servings is required")
    private Integer servings;
}