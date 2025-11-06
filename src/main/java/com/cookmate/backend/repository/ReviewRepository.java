package com.cookmate.backend.repository;

import com.cookmate.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query(value = "SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.recipe " +
           "LEFT JOIN FETCH r.user " +
           "WHERE r.recipe.id = :recipeId",
           countQuery = "SELECT COUNT(r) FROM Review r WHERE r.recipe.id = :recipeId")
    Page<Review> findByRecipe_Id(@Param("recipeId") Long recipeId, Pageable pageable);
    
    @Query(value = "SELECT r FROM Review r " +
           "LEFT JOIN FETCH r.recipe " +
           "LEFT JOIN FETCH r.user " +
           "WHERE r.user.id = :userId",
           countQuery = "SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    Page<Review> findByUser_Id(@Param("userId") Long userId, Pageable pageable);
    
    Optional<Review> findByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    Boolean existsByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.recipe.id = :recipeId")
    Double calculateAverageRating(@Param("recipeId") Long recipeId);
    
    Long countByRecipe_Id(Long recipeId);
}