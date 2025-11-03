package com.cookmate.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemRequest {
    
    @NotBlank(message = "Ingredient name is required")
    private String ingredientName;
    
    private BigDecimal quantity;
    private String unit;
    private String category;
}