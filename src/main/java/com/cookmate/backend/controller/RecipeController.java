package com.cookmate.backend.controller;

import com.cookmate.backend.dto.*;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.repository.RecipeRepository;
import com.cookmate.backend.exception.ResourceNotFoundException;
import com.cookmate.backend.service.FileStorageService;
import com.cookmate.backend.service.IngredientSubstitutionService;
import com.cookmate.backend.service.RecipeService;
import com.cookmate.backend.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private IngredientSubstitutionService substitutionService;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxFileSize;
    
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Image file is required"));
            }
            
            // Validate file size (5MB max)
            if (image.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Image size should be less than 5MB"));
            }
            
            // Validate file type
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File must be an image"));
            }
            
            // Store the file and get the filename
            String filename = fileStorageService.storeFile(image);
            String imageUrl = "/uploads/" + filename;
            
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (RuntimeException e) {
            // Log the error for debugging
            System.err.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Unexpected error uploading image: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        }
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipeDto> createRecipe(
            @RequestPart("recipe") @Valid RecipeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) throws IOException {
        if (image != null) {
            request.setImageData(image.getBytes());
            request.setImageFilename(image.getOriginalFilename());
            request.setImageContentType(image.getContentType());
        }
        RecipeDto recipe = recipeService.createRecipe(request, authentication);
        return new ResponseEntity<>(recipe, HttpStatus.CREATED);
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipeDto> updateRecipe(
            @PathVariable Long id,
            @RequestPart("recipe") @Valid RecipeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication) throws IOException {
        if (image != null) {
            request.setImageData(image.getBytes());
            request.setImageFilename(image.getOriginalFilename());
            request.setImageContentType(image.getContentType());
        }
        RecipeDto recipe = recipeService.updateRecipe(id, request, authentication);
        return ResponseEntity.ok(recipe);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteRecipe(
            @PathVariable Long id,
            Authentication authentication) {
        recipeService.deleteRecipe(id, authentication);
        return ResponseEntity.ok(new ApiResponse(true, "Recipe deleted successfully"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(
            @PathVariable Long id,
            Authentication authentication) {
        RecipeDto recipe = recipeService.getRecipeById(id, authentication);
        return ResponseEntity.ok(recipe);
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<RecipeDto>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        PageResponse<RecipeDto> recipes = recipeService.getAllRecipes(page, size, sortBy, sortDir);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/search")
    public ResponseEntity<PageResponse<RecipeDto>> searchRecipes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recipeService.searchRecipes(keyword, page, size);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<PageResponse<RecipeDto>> filterRecipes(
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(required = false) Boolean isVegetarian,
            @RequestParam(required = false) Boolean isVegan,
            @RequestParam(required = false) Boolean isGlutenFree,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recipeService.filterRecipes(
                cuisineType, mealType, difficultyLevel, maxTime,
                isVegetarian, isVegan, isGlutenFree, page, size);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/search-by-ingredients")
    public ResponseEntity<PageResponse<RecipeDto>> searchByIngredients(
            @RequestParam List<String> ingredients,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recipeService.searchByIngredients(ingredients, page, size);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<PageResponse<RecipeDto>> getTopRatedRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recipeService.getTopRatedRecipes(page, size);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/most-viewed")
    public ResponseEntity<PageResponse<RecipeDto>> getMostViewedRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recipeService.getMostViewedRecipes(page, size);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<PageResponse<RecipeDto>> getRecentRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recipeService.getRecentRecipes(page, size);
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/my-recipes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<RecipeDto>> getMyRecipes(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recipeService.getMyRecipes(authentication, page, size);
        return ResponseEntity.ok(recipes);
    }

    // AI-Powered Features
    
    /**
     * Get recipe recommendations based on type (personalized, history, preferences, trending)
     */
    @GetMapping("/recommendations")
    public ResponseEntity<PageResponse<RecipeDto>> getRecommendations(
            Authentication authentication,
            @RequestParam(defaultValue = "personalized") String type,
            @RequestParam(defaultValue = "10") int limit) {
        PageResponse<RecipeDto> recommendations = recommendationService.getRecommendations(type, authentication, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get recommendations based on user's viewing history
     */
    @GetMapping("/recommendations/history")
    public ResponseEntity<PageResponse<RecipeDto>> getRecommendationsByHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit) {
        PageResponse<RecipeDto> recommendations = recommendationService.getRecommendationsByHistory(authentication, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get recommendations based on user preferences
     */
    @GetMapping("/recommendations/preferences")
    public ResponseEntity<PageResponse<RecipeDto>> getRecommendationsByPreferences(
            Authentication authentication,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) Boolean isVegetarian,
            @RequestParam(required = false) Boolean isVegan,
            @RequestParam(required = false) Boolean isGlutenFree,
            @RequestParam(defaultValue = "10") int limit) {
        
        java.util.HashMap<String, Object> preferences = new java.util.HashMap<>();
        if (cuisineType != null) preferences.put("cuisineType", cuisineType);
        if (mealType != null) preferences.put("mealType", mealType);
        if (isVegetarian != null) preferences.put("isVegetarian", isVegetarian);
        if (isVegan != null) preferences.put("isVegan", isVegan);
        if (isGlutenFree != null) preferences.put("isGlutenFree", isGlutenFree);
        
        PageResponse<RecipeDto> recommendations = recommendationService.getRecommendationsByPreferences(
                authentication, preferences, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get trending recipes
     */
    @GetMapping("/recommendations/trending")
    public ResponseEntity<PageResponse<RecipeDto>> getTrendingRecipes(
            @RequestParam(defaultValue = "10") int limit) {
        PageResponse<RecipeDto> recommendations = recommendationService.getTrendingRecipes(limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get ingredient substitutions
     */
    @GetMapping("/substitutions")
    public ResponseEntity<IngredientSubstitutionDto> getIngredientSubstitutions(
            @RequestParam String ingredient,
            @RequestParam(defaultValue = "false") boolean useAI) {
        IngredientSubstitutionDto substitutions = substitutionService.suggest(ingredient, useAI);
        return ResponseEntity.ok(substitutions);
    }

}