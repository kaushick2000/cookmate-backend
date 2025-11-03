package com.cookmate.backend.repository;

import com.cookmate.backend.entity.MealPlanRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealPlanRecipeRepository extends JpaRepository<MealPlanRecipe, Long> {
    
    List<MealPlanRecipe> findByMealPlan_Id(Long mealPlanId);
    
    List<MealPlanRecipe> findByMealPlan_IdAndPlannedDate(Long mealPlanId, LocalDate date);
    
    void deleteByMealPlan_Id(Long mealPlanId);
}