package com.cookmate.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "shopping_list_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
    
    @NotBlank
    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String ingredientName;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;
    
    @Column(length = 50)
    private String unit;
    
    @Column(name = "is_purchased")
    private Boolean isPurchased = false;
    
    @Column(length = 50)
    private String category;
}