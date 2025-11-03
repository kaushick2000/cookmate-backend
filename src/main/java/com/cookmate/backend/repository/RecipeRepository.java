package com.cookmate.backend.repository;

import com.cookmate.backend.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // Search by title or description
    @Query("SELECT r FROM Recipe r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Recipe> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Filter by cuisine type
    Page<Recipe> findByCuisineType(String cuisineType, Pageable pageable);
    
    // Filter by meal type
    Page<Recipe> findByMealType(String mealType, Pageable pageable);
    
    // Filter by difficulty level
    Page<Recipe> findByDifficultyLevel(String difficultyLevel, Pageable pageable);
    
    // Filter by dietary preferences
    Page<Recipe> findByIsVegetarian(Boolean isVegetarian, Pageable pageable);
    Page<Recipe> findByIsVegan(Boolean isVegan, Pageable pageable);
    Page<Recipe> findByIsGlutenFree(Boolean isGlutenFree, Pageable pageable);
    Page<Recipe> findByIsDairyFree(Boolean isDairyFree, Pageable pageable);
    
    // Search by ingredients
    @Query("SELECT DISTINCT r FROM Recipe r " +
           "JOIN r.recipeIngredients ri " +
           "JOIN ri.ingredient i " +
           "WHERE LOWER(i.name) IN :ingredientNames")
    Page<Recipe> findByIngredients(@Param("ingredientNames") List<String> ingredientNames, Pageable pageable);
    
    // Advanced search with multiple filters
    @Query("SELECT r FROM Recipe r WHERE " +
           "(:cuisineType IS NULL OR r.cuisineType = :cuisineType) AND " +
           "(:mealType IS NULL OR r.mealType = :mealType) AND " +
           "(:difficultyLevel IS NULL OR r.difficultyLevel = :difficultyLevel) AND " +
           "(:maxTime IS NULL OR r.totalTime <= :maxTime) AND " +
           "(:isVegetarian IS NULL OR r.isVegetarian = :isVegetarian) AND " +
           "(:isVegan IS NULL OR r.isVegan = :isVegan) AND " +
           "(:isGlutenFree IS NULL OR r.isGlutenFree = :isGlutenFree)")
    Page<Recipe> findByFilters(
            @Param("cuisineType") String cuisineType,
            @Param("mealType") String mealType,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("maxTime") Integer maxTime,
            @Param("isVegetarian") Boolean isVegetarian,
            @Param("isVegan") Boolean isVegan,
            @Param("isGlutenFree") Boolean isGlutenFree,
            Pageable pageable
    );
    
    // Top rated recipes
    Page<Recipe> findByOrderByAverageRatingDesc(Pageable pageable);
    
    // Most viewed recipes
    Page<Recipe> findByOrderByViewCountDesc(Pageable pageable);
    
    // Recent recipes
    Page<Recipe> findByOrderByCreatedAtDesc(Pageable pageable);
    
    // Recipes by creator
    Page<Recipe> findByCreatedBy_Id(Long userId, Pageable pageable);
}