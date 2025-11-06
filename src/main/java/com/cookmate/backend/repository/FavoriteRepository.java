package com.cookmate.backend.repository;

import com.cookmate.backend.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    @Query(value = "SELECT f FROM Favorite f " +
           "LEFT JOIN FETCH f.recipe r " +
           "LEFT JOIN FETCH r.createdBy " +
           "WHERE f.user.id = :userId",
           countQuery = "SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId")
    Page<Favorite> findByUser_Id(@Param("userId") Long userId, Pageable pageable);
    
    Optional<Favorite> findByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    Boolean existsByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    void deleteByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    Long countByRecipe_Id(Long recipeId);
}