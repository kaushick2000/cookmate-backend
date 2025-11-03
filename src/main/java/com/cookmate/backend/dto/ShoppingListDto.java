package com.cookmate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListDto {
    
    private Long id;
    private Long userId;
    private String name;
    private LocalDateTime createdAt;
    private List<ShoppingListItemDto> items;
}