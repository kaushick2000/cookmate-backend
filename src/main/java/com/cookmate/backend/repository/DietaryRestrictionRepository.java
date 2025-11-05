package com.cookmate.backend.repository;

import com.cookmate.backend.entity.DietaryRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DietaryRestrictionRepository extends JpaRepository<DietaryRestriction, Long> {
    Optional<DietaryRestriction> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
