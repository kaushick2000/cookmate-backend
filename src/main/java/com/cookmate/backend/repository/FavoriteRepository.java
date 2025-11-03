package com.cookmate.backend.repository;

import com.cookmate.backend.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Page<Favorite> findByUser_Id(Long userId, Pageable pageable);
    
    Optional<Favorite> findByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    Boolean existsByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    void deleteByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    Long countByRecipe_Id(Long recipeId);
}