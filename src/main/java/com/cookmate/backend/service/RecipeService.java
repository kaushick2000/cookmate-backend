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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    
    @Autowired
    private InstructionRepository instructionRepository;
    
    @Autowired
    private RecentlyViewedRepository recentlyViewedRepository;
    
    @Transactional
    public RecipeDto createRecipe(RecipeRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
        
        Recipe recipe = new Recipe();
        mapRequestToRecipe(request, recipe);
        recipe.setCreatedBy(user);
        
        // Calculate total time
        Integer totalTime = (request.getPrepTime() != null ? request.getPrepTime() : 0) +
                          (request.getCookTime() != null ? request.getCookTime() : 0);
        recipe.setTotalTime(totalTime);
        
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        // Save ingredients
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            saveRecipeIngredients(savedRecipe, request.getIngredients());
        }
        
        // Save instructions
        if (request.getInstructions() != null && !request.getInstructions().isEmpty()) {
            saveInstructions(savedRecipe, request.getInstructions());
        }
        
        return convertToDto(savedRecipe);
    }
    
    @Transactional
    public RecipeDto updateRecipe(Long id, RecipeRequest request, Authentication authentication) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if user is the creator or admin
        if (!recipe.getCreatedBy().getId().equals(userDetails.getId()) &&
            !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("You don't have permission to update this recipe");
        }
        
        mapRequestToRecipe(request, recipe);
        
        // Calculate total time
        Integer totalTime = (request.getPrepTime() != null ? request.getPrepTime() : 0) +
                          (request.getCookTime() != null ? request.getCookTime() : 0);
        recipe.setTotalTime(totalTime);
        
        Recipe updatedRecipe = recipeRepository.save(recipe);
        
        // Update ingredients
        if (request.getIngredients() != null) {
            recipeIngredientRepository.deleteByRecipe_Id(id);
            saveRecipeIngredients(updatedRecipe, request.getIngredients());
        }
        
        // Update instructions
        if (request.getInstructions() != null) {
            instructionRepository.deleteByRecipe_Id(id);
            saveInstructions(updatedRecipe, request.getInstructions());
        }
        
        return convertToDto(updatedRecipe);
    }
    
    @Transactional
    public void deleteRecipe(Long id, Authentication authentication) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if user is the creator or admin
        if (!recipe.getCreatedBy().getId().equals(userDetails.getId()) &&
            !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("You don't have permission to delete this recipe");
        }
        
        recipeRepository.delete(recipe);
    }
    
    @Transactional
    public RecipeDto getRecipeById(Long id, Authentication authentication) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));
        
        // Increment view count
        recipe.setViewCount(recipe.getViewCount() + 1);
        recipeRepository.save(recipe);
        
        // Add to recently viewed if user is authenticated
        if (authentication != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId()).orElse(null);
            
            if (user != null) {
                RecentlyViewed recentlyViewed = recentlyViewedRepository
                        .findByUser_IdAndRecipe_Id(user.getId(), recipe.getId())
                        .orElse(new RecentlyViewed());
                
                recentlyViewed.setUser(user);
                recentlyViewed.setRecipe(recipe);
                recentlyViewedRepository.save(recentlyViewed);
            }
        }
        
        return convertToDto(recipe);
    }
    
    public PageResponse<RecipeDto> getAllRecipes(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return convertToPageResponse(recipePage);
    }
    
    public PageResponse<RecipeDto> searchRecipes(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.searchByKeyword(keyword, pageable);
        return convertToPageResponse(recipePage);
    }
    
    public PageResponse<RecipeDto> filterRecipes(
            String cuisineType, String mealType, String difficultyLevel, 
            Integer maxTime, Boolean isVegetarian, Boolean isVegan, 
            Boolean isGlutenFree, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findByFilters(
                cuisineType, mealType, difficultyLevel, maxTime, 
                isVegetarian, isVegan, isGlutenFree, pageable);
        
        return convertToPageResponse(recipePage);
    }
    
    public PageResponse<RecipeDto> searchByIngredients(List<String> ingredients, int page, int size) {
        List<String> lowerCaseIngredients = ingredients.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findByIngredients(lowerCaseIngredients, pageable);
        return convertToPageResponse(recipePage);
    }
    
    public PageResponse<RecipeDto> getTopRatedRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findByOrderByAverageRatingDesc(pageable);
        return convertToPageResponse(recipePage);
    }
    
    public PageResponse<RecipeDto> getMostViewedRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findByOrderByViewCountDesc(pageable);
        return convertToPageResponse(recipePage);
    }
    
    public PageResponse<RecipeDto> getRecentRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findByOrderByCreatedAtDesc(pageable);
        return convertToPageResponse(recipePage);
    }
    
    public PageResponse<RecipeDto> getMyRecipes(Authentication authentication, int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findByCreatedBy_Id(userDetails.getId(), pageable);
        return convertToPageResponse(recipePage);
    }
    
    // Helper methods
    
    private void mapRequestToRecipe(RecipeRequest request, Recipe recipe) {
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setCuisineType(request.getCuisineType());
        recipe.setMealType(request.getMealType());
        recipe.setDifficultyLevel(request.getDifficultyLevel());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setCookTime(request.getCookTime());
        recipe.setServings(request.getServings());
        recipe.setCalories(request.getCalories());
        recipe.setProtein(request.getProtein());
        recipe.setCarbs(request.getCarbs());
        recipe.setFat(request.getFat());
        recipe.setFiber(request.getFiber());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setVideoUrl(request.getVideoUrl());
        recipe.setIsVegetarian(request.getIsVegetarian());
        recipe.setIsVegan(request.getIsVegan());
        recipe.setIsGlutenFree(request.getIsGlutenFree());
        recipe.setIsDairyFree(request.getIsDairyFree());
    }
    
    private void saveRecipeIngredients(Recipe recipe, List<RecipeIngredientRequest> ingredientRequests) {
        for (RecipeIngredientRequest ingredientRequest : ingredientRequests) {
            // Find or create ingredient
            Ingredient ingredient = ingredientRepository
                    .findByNameIgnoreCase(ingredientRequest.getIngredientName())
                    .orElseGet(() -> {
                        Ingredient newIngredient = new Ingredient();
                        newIngredient.setName(ingredientRequest.getIngredientName());
                        return ingredientRepository.save(newIngredient);
                    });
            
            // Create recipe ingredient
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(ingredientRequest.getQuantity());
            recipeIngredient.setUnit(ingredientRequest.getUnit());
            recipeIngredient.setNotes(ingredientRequest.getNotes());
            
            recipeIngredientRepository.save(recipeIngredient);
        }
    }
    
    private void saveInstructions(Recipe recipe, List<InstructionRequest> instructionRequests) {
        for (InstructionRequest instructionRequest : instructionRequests) {
            Instruction instruction = new Instruction();
            instruction.setRecipe(recipe);
            instruction.setStepNumber(instructionRequest.getStepNumber());
            instruction.setInstruction(instructionRequest.getInstruction());
            instruction.setTimerMinutes(instructionRequest.getTimerMinutes());
            instruction.setImageUrl(instructionRequest.getImageUrl());
            
            instructionRepository.save(instruction);
        }
    }
    
    public RecipeDto convertToDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setCuisineType(recipe.getCuisineType());
        dto.setMealType(recipe.getMealType());
        dto.setDifficultyLevel(recipe.getDifficultyLevel());
        dto.setPrepTime(recipe.getPrepTime());
        dto.setCookTime(recipe.getCookTime());
        dto.setTotalTime(recipe.getTotalTime());
        dto.setServings(recipe.getServings());
        dto.setCalories(recipe.getCalories());
        dto.setProtein(recipe.getProtein());
        dto.setCarbs(recipe.getCarbs());
        dto.setFat(recipe.getFat());
        dto.setFiber(recipe.getFiber());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setVideoUrl(recipe.getVideoUrl());
        dto.setIsVegetarian(recipe.getIsVegetarian());
        dto.setIsVegan(recipe.getIsVegan());
        dto.setIsGlutenFree(recipe.getIsGlutenFree());
        dto.setIsDairyFree(recipe.getIsDairyFree());
        dto.setAverageRating(recipe.getAverageRating());
        dto.setTotalReviews(recipe.getTotalReviews());
        dto.setViewCount(recipe.getViewCount());
        dto.setCreatedAt(recipe.getCreatedAt());
        
        if (recipe.getCreatedBy() != null) {
            dto.setCreatedById(recipe.getCreatedBy().getId());
            dto.setCreatedByUsername(recipe.getCreatedBy().getUsername());
        }
        
        // Convert ingredients
        List<RecipeIngredientDto> ingredientDtos = recipe.getRecipeIngredients().stream()
                .map(this::convertToIngredientDto)
                .collect(Collectors.toList());
        dto.setIngredients(ingredientDtos);
        
        // Convert instructions
        List<InstructionDto> instructionDtos = recipe.getInstructions().stream()
                .map(this::convertToInstructionDto)
                .collect(Collectors.toList());
        dto.setInstructions(instructionDtos);
        
        return dto;
    }
    
    private RecipeIngredientDto convertToIngredientDto(RecipeIngredient recipeIngredient) {
        RecipeIngredientDto dto = new RecipeIngredientDto();
        dto.setId(recipeIngredient.getId());
        dto.setIngredientId(recipeIngredient.getIngredient().getId());
        dto.setIngredientName(recipeIngredient.getIngredient().getName());
        dto.setQuantity(recipeIngredient.getQuantity());
        dto.setUnit(recipeIngredient.getUnit());
        dto.setNotes(recipeIngredient.getNotes());
        return dto;
    }
    
    private InstructionDto convertToInstructionDto(Instruction instruction) {
        InstructionDto dto = new InstructionDto();
        dto.setId(instruction.getId());
        dto.setStepNumber(instruction.getStepNumber());
        dto.setInstruction(instruction.getInstruction());
        dto.setTimerMinutes(instruction.getTimerMinutes());
        dto.setImageUrl(instruction.getImageUrl());
        return dto;
    }
    
    private PageResponse<RecipeDto> convertToPageResponse(Page<Recipe> recipePage) {
        List<RecipeDto> content = recipePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                recipePage.getNumber(),
                recipePage.getSize(),
                recipePage.getTotalElements(),
                recipePage.getTotalPages(),
                recipePage.isLast()
        );
    }
}