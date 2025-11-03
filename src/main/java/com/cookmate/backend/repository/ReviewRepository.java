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
    
    Page<Review> findByRecipe_Id(Long recipeId, Pageable pageable);
    
    Page<Review> findByUser_Id(Long userId, Pageable pageable);
    
    Optional<Review> findByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    Boolean existsByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.recipe.id = :recipeId")
    Double calculateAverageRating(@Param("recipeId") Long recipeId);
    
    Long countByRecipe_Id(Long recipeId);
}