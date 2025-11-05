package com.cookmate.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredient_substitutions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSubstitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "ingredient", nullable = false, length = 100)
    private String ingredient;

    @Column(name = "substitution", nullable = false, length = 100)
    private String substitution;

    @Column(name = "use_ai", nullable = false)
    private Boolean useAI = false;
}