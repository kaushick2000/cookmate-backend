package com.cookmate.backend.service;

import com.cookmate.backend.dto.*;
import com.cookmate.backend.entity.*;
import com.cookmate.backend.exception.BadRequestException;
import com.cookmate.backend.exception.ResourceNotFoundException;
import com.cookmate.backend.exception.UnauthorizedException;
import com.cookmate.backend.repository.*;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealPlanService {
    
    @Autowired
    private MealPlanRepository mealPlanRepository;
    
    @Autowired
    private MealPlanRecipeRepository mealPlanRecipeRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public MealPlanDto createMealPlan(MealPlanRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(user);
        mealPlan.setName(request.getName());
        mealPlan.setStartDate(request.getStartDate());
        mealPlan.setEndDate(request.getEndDate());
        
        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);
        
        // Add recipes to meal plan
        if (request.getMeals() != null && !request.getMeals().isEmpty()) {
            saveMealPlanRecipes(savedMealPlan, request.getMeals());
        }
        
        return convertToDto(savedMealPlan);
    }
    
    @Transactional
    public MealPlanDto updateMealPlan(Long id, MealPlanRequest request, Authentication authentication) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!mealPlan.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to update this meal plan");
        }
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        
        mealPlan.setName(request.getName());
        mealPlan.setStartDate(request.getStartDate());
        mealPlan.setEndDate(request.getEndDate());
        
        // Update recipes - delete existing ones first
        // With orphanRemoval = true, clearing the collection will automatically delete orphaned entities
        mealPlan.getMealPlanRecipes().clear();
        
        // Save the meal plan to commit the deletion (orphan removal will delete the old meals)
        MealPlan updatedMealPlan = mealPlanRepository.save(mealPlan);
        
        // Flush to ensure deletion is committed before adding new ones
        mealPlanRepository.flush();
        
        // Add new recipes
        if (request.getMeals() != null && !request.getMeals().isEmpty()) {
            saveMealPlanRecipes(updatedMealPlan, request.getMeals());
        }
        
        // Flush to ensure all new meals are saved
        mealPlanRepository.flush();
        
        // Reload the meal plan to get the updated recipes
        updatedMealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", id));
        
        // Eagerly load mealPlanRecipes to avoid LazyInitializationException
        updatedMealPlan.getMealPlanRecipes().size();
        
        return convertToDto(updatedMealPlan);
    }
    
    @Transactional
    public ApiResponse deleteMealPlan(Long id, Authentication authentication) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!mealPlan.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this meal plan");
        }
        
        mealPlanRepository.delete(mealPlan);
        
        return new ApiResponse(true, "Meal plan deleted successfully");
    }
    
    @Transactional(readOnly = true)
    public MealPlanDto getMealPlanById(Long id, Authentication authentication) {
        MealPlan mealPlan = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MealPlan", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!mealPlan.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to view this meal plan");
        }
        
        // Eagerly load mealPlanRecipes to avoid LazyInitializationException
        mealPlan.getMealPlanRecipes().size();
        
        return convertToDto(mealPlan);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<MealPlanDto> getUserMealPlans(Authentication authentication, int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<MealPlan> mealPlanPage = mealPlanRepository.findByUser_Id(userDetails.getId(), pageable);
        
        // Eagerly load mealPlanRecipes to avoid LazyInitializationException
        List<MealPlan> mealPlans = mealPlanPage.getContent();
        for (MealPlan mealPlan : mealPlans) {
            // Initialize the lazy collection within the transaction
            mealPlan.getMealPlanRecipes().size();
        }
        
        List<MealPlanDto> content = mealPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                mealPlanPage.getNumber(),
                mealPlanPage.getSize(),
                mealPlanPage.getTotalElements(),
                mealPlanPage.getTotalPages(),
                mealPlanPage.isLast()
        );
    }
    
    @Transactional(readOnly = true)
    public List<MealPlanDto> getActiveMealPlans(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        LocalDate today = LocalDate.now();
        
        List<MealPlan> mealPlans = mealPlanRepository
                .findByUser_IdAndEndDateAfter(userDetails.getId(), today);
        
        // Eagerly load mealPlanRecipes to avoid LazyInitializationException
        for (MealPlan mealPlan : mealPlans) {
            // Initialize the lazy collection within the transaction
            mealPlan.getMealPlanRecipes().size();
        }
        
        return mealPlans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private void saveMealPlanRecipes(MealPlan mealPlan, List<MealPlanRecipeRequest> recipeRequests) {
        for (MealPlanRecipeRequest recipeRequest : recipeRequests) {
            Recipe recipe = recipeRepository.findById(recipeRequest.getRecipeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeRequest.getRecipeId()));
            
            MealPlanRecipe mealPlanRecipe = new MealPlanRecipe();
            mealPlanRecipe.setMealPlan(mealPlan);
            mealPlanRecipe.setRecipe(recipe);
            mealPlanRecipe.setPlannedDate(recipeRequest.getPlannedDate());
            mealPlanRecipe.setMealTime(recipeRequest.getMealTime());
            mealPlanRecipe.setServings(recipeRequest.getServings());
            
            mealPlanRecipeRepository.save(mealPlanRecipe);
        }
    }
    
    private MealPlanDto convertToDto(MealPlan mealPlan) {
        MealPlanDto dto = new MealPlanDto();
        dto.setId(mealPlan.getId());
        dto.setUserId(mealPlan.getUser().getId());
        dto.setName(mealPlan.getName());
        dto.setStartDate(mealPlan.getStartDate());
        dto.setEndDate(mealPlan.getEndDate());
        dto.setCreatedAt(mealPlan.getCreatedAt());
        
        // Safely handle mealPlanRecipes collection
        try {
            List<MealPlanRecipeDto> meals = mealPlan.getMealPlanRecipes().stream()
                    .map(this::convertToMealRecipeDto)
                    .collect(Collectors.toList());
            dto.setMeals(meals);
        } catch (Exception e) {
            // If collection is not initialized, set empty list
            System.err.println("Warning: mealPlanRecipes collection not initialized for meal plan " + mealPlan.getId());
            dto.setMeals(new ArrayList<>());
        }
        
        return dto;
    }
    
    private MealPlanRecipeDto convertToMealRecipeDto(MealPlanRecipe mealPlanRecipe) {
        MealPlanRecipeDto dto = new MealPlanRecipeDto();
        dto.setId(mealPlanRecipe.getId());
        dto.setRecipeId(mealPlanRecipe.getRecipe().getId());
        dto.setRecipeTitle(mealPlanRecipe.getRecipe().getTitle());
        dto.setRecipeImageUrl(mealPlanRecipe.getRecipe().getImageUrl());
        dto.setPlannedDate(mealPlanRecipe.getPlannedDate());
        dto.setMealTime(mealPlanRecipe.getMealTime());
        dto.setServings(mealPlanRecipe.getServings());
        return dto;
    }
}