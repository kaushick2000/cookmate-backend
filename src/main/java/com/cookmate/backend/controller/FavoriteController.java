package com.cookmate.backend.controller;

import com.cookmate.backend.dto.ApiResponse;
import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/{recipeId}")
    public ResponseEntity<ApiResponse> addFavorite(
            @PathVariable Long recipeId,
            Authentication authentication) {
        ApiResponse response = favoriteService.addFavorite(recipeId, authentication);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<ApiResponse> removeFavorite(
            @PathVariable Long recipeId,
            Authentication authentication) {
        ApiResponse response = favoriteService.removeFavorite(recipeId, authentication);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<RecipeDto>> getUserFavorites(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> favorites = favoriteService.getUserFavorites(authentication, page, size);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/check/{recipeId}")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Long recipeId,
            Authentication authentication) {
        Boolean isFavorite = favoriteService.isFavorite(recipeId, authentication);
        return ResponseEntity.ok(isFavorite);
    }
}