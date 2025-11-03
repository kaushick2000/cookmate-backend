package com.cookmate.backend.repository;

import com.cookmate.backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByNameIgnoreCase(String name);

    List<Ingredient> findByCategory(String category);

    @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Ingredient> searchByKeyword(@Param("keyword") String keyword);

    Boolean existsByNameIgnoreCase(String name);
}