package com.cookmate.backend.repository;

import com.cookmate.backend.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    
    List<RecipeIngredient> findByRecipe_Id(Long recipeId);
    
    void deleteByRecipe_Id(Long recipeId);
}