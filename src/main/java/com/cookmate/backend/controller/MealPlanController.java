package com.cookmate.backend.controller;

import com.cookmate.backend.dto.ApiResponse;
import com.cookmate.backend.dto.MealPlanDto;
import com.cookmate.backend.dto.MealPlanRequest;
import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
public class MealPlanController {
    
    @Autowired
    private MealPlanService mealPlanService;
    
    @PostMapping
    public ResponseEntity<MealPlanDto> createMealPlan(
            @Valid @RequestBody MealPlanRequest request,
            Authentication authentication) {
        MealPlanDto mealPlan = mealPlanService.createMealPlan(request, authentication);
        return new ResponseEntity<>(mealPlan, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MealPlanDto> updateMealPlan(
            @PathVariable Long id,
            @Valid @RequestBody MealPlanRequest request,
            Authentication authentication) {
        MealPlanDto mealPlan = mealPlanService.updateMealPlan(id, request, authentication);
        return ResponseEntity.ok(mealPlan);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMealPlan(
            @PathVariable Long id,
            Authentication authentication) {
        ApiResponse response = mealPlanService.deleteMealPlan(id, authentication);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MealPlanDto> getMealPlanById(
            @PathVariable Long id,
            Authentication authentication) {
        MealPlanDto mealPlan = mealPlanService.getMealPlanById(id, authentication);
        return ResponseEntity.ok(mealPlan);
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<MealPlanDto>> getUserMealPlans(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<MealPlanDto> mealPlans = mealPlanService.getUserMealPlans(authentication, page, size);
        return ResponseEntity.ok(mealPlans);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<MealPlanDto>> getActiveMealPlans(Authentication authentication) {
        List<MealPlanDto> mealPlans = mealPlanService.getActiveMealPlans(authentication);
        return ResponseEntity.ok(mealPlans);
    }
}