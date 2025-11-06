package com.cookmate.backend.repository;

import com.cookmate.backend.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
    // Override findById to include JOIN FETCH for createdBy only
    @Query("SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.id = :id")
    Optional<Recipe> findByIdWithDetails(@Param("id") Long id);
    
    // Override findAll to include JOIN FETCH for createdBy only
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy",
           countQuery = "SELECT COUNT(r) FROM Recipe r")
    Page<Recipe> findAllWithDetails(Pageable pageable);
    
    // Search by title or description
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Recipe> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Filter by cuisine type
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.cuisineType = :cuisineType",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.cuisineType = :cuisineType")
    Page<Recipe> findByCuisineType(@Param("cuisineType") String cuisineType, Pageable pageable);
    
    // Filter by meal type
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.mealType = :mealType",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.mealType = :mealType")
    Page<Recipe> findByMealType(@Param("mealType") String mealType, Pageable pageable);
    
    // Filter by difficulty level
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.difficultyLevel = :difficultyLevel",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.difficultyLevel = :difficultyLevel")
    Page<Recipe> findByDifficultyLevel(@Param("difficultyLevel") String difficultyLevel, Pageable pageable);
    
    // Filter by dietary preferences
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.isVegetarian = :isVegetarian",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.isVegetarian = :isVegetarian")
    Page<Recipe> findByIsVegetarian(@Param("isVegetarian") Boolean isVegetarian, Pageable pageable);
    
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.isVegan = :isVegan",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.isVegan = :isVegan")
    Page<Recipe> findByIsVegan(@Param("isVegan") Boolean isVegan, Pageable pageable);
    
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.isGlutenFree = :isGlutenFree",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.isGlutenFree = :isGlutenFree")
    Page<Recipe> findByIsGlutenFree(@Param("isGlutenFree") Boolean isGlutenFree, Pageable pageable);
    
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.isDairyFree = :isDairyFree",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.isDairyFree = :isDairyFree")
    Page<Recipe> findByIsDairyFree(@Param("isDairyFree") Boolean isDairyFree, Pageable pageable);
    
    // Search by ingredients
    @Query(value = "SELECT DISTINCT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "JOIN r.recipeIngredients ri " +
           "JOIN ri.ingredient i " +
           "WHERE LOWER(i.name) IN :ingredientNames",
           countQuery = "SELECT COUNT(DISTINCT r) FROM Recipe r " +
           "JOIN r.recipeIngredients ri " +
           "JOIN ri.ingredient i " +
           "WHERE LOWER(i.name) IN :ingredientNames")
    Page<Recipe> findByIngredients(@Param("ingredientNames") List<String> ingredientNames, Pageable pageable);
    
    // Advanced search with multiple filters
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE (:cuisineType IS NULL OR r.cuisineType = :cuisineType) AND " +
           "(:mealType IS NULL OR r.mealType = :mealType) AND " +
           "(:difficultyLevel IS NULL OR r.difficultyLevel = :difficultyLevel) AND " +
           "(:maxTime IS NULL OR r.totalTime <= :maxTime) AND " +
           "(:isVegetarian IS NULL OR r.isVegetarian = :isVegetarian) AND " +
           "(:isVegan IS NULL OR r.isVegan = :isVegan) AND " +
           "(:isGlutenFree IS NULL OR r.isGlutenFree = :isGlutenFree)",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE " +
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
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "ORDER BY r.averageRating DESC",
           countQuery = "SELECT COUNT(r) FROM Recipe r")
    Page<Recipe> findByOrderByAverageRatingDesc(Pageable pageable);
    
    // Most viewed recipes
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "ORDER BY r.viewCount DESC",
           countQuery = "SELECT COUNT(r) FROM Recipe r")
    Page<Recipe> findByOrderByViewCountDesc(Pageable pageable);
    
    // Recent recipes
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "ORDER BY r.createdAt DESC",
           countQuery = "SELECT COUNT(r) FROM Recipe r")
    Page<Recipe> findByOrderByCreatedAtDesc(Pageable pageable);
    
    // Recipes by creator
    @Query(value = "SELECT r FROM Recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE r.createdBy.id = :userId",
           countQuery = "SELECT COUNT(r) FROM Recipe r WHERE r.createdBy.id = :userId")
    Page<Recipe> findByCreatedBy_Id(@Param("userId") Long userId, Pageable pageable);
}