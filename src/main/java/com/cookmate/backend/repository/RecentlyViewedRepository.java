package com.cookmate.backend.repository;

import com.cookmate.backend.entity.RecentlyViewed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed, Long> {
    
    Page<RecentlyViewed> findByUser_IdOrderByViewedAtDesc(Long userId, Pageable pageable);
    
    Optional<RecentlyViewed> findByUser_IdAndRecipe_Id(Long userId, Long recipeId);
    
    void deleteByUser_IdAndRecipe_Id(Long userId, Long recipeId);
}