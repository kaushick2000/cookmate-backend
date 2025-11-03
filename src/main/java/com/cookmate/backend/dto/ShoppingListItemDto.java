package com.cookmate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemDto {
    
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
    private Boolean isPurchased;
    private String category;
}