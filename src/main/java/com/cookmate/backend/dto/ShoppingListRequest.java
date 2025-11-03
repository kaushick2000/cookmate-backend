package com.cookmate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListRequest {
    
    private String name;
    private List<Long> recipeIds;
    private List<ShoppingListItemRequest> customItems;
}