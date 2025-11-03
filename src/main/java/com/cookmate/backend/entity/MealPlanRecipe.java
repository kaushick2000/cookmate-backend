package com.cookmate.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "meal_plan_recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanRecipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;
    
    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;
    
    @Column(name = "meal_time", length = 20)
    private String mealTime;
    
    private Integer servings = 1;
}