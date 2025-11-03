package com.cookmate.backend.repository;

import com.cookmate.backend.entity.MealPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    
    Page<MealPlan> findByUser_Id(Long userId, Pageable pageable);
    
    List<MealPlan> findByUser_IdAndStartDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<MealPlan> findByUser_IdAndEndDateAfter(Long userId, LocalDate date);
}