package com.cookmate.backend.service;

import com.cookmate.backend.dto.ApiResponse;
import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.entity.Favorite;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.exception.BadRequestException;
import com.cookmate.backend.exception.ResourceNotFoundException;
import com.cookmate.backend.repository.FavoriteRepository;
import com.cookmate.backend.repository.RecipeRepository;
import com.cookmate.backend.repository.UserRepository;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecipeService recipeService;
    
    @Transactional
    public ApiResponse addFavorite(Long recipeId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
        
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));
        
        if (favoriteRepository.existsByUser_IdAndRecipe_Id(user.getId(), recipeId)) {
            throw new BadRequestException("Recipe is already in favorites");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setRecipe(recipe);
        
        favoriteRepository.save(favorite);
        
        return new ApiResponse(true, "Recipe added to favorites");
    }
    
    @Transactional
    public ApiResponse removeFavorite(Long recipeId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!favoriteRepository.existsByUser_IdAndRecipe_Id(userDetails.getId(), recipeId)) {
            throw new ResourceNotFoundException("Favorite not found");
        }
        
        favoriteRepository.deleteByUser_IdAndRecipe_Id(userDetails.getId(), recipeId);
        
        return new ApiResponse(true, "Recipe removed from favorites");
    }
    
    public PageResponse<RecipeDto> getUserFavorites(Authentication authentication, int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Favorite> favoritePage = favoriteRepository.findByUser_Id(userDetails.getId(), pageable);
        
        List<RecipeDto> content = favoritePage.getContent().stream()
                .map(favorite -> recipeService.convertToDto(favorite.getRecipe()))
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                favoritePage.getNumber(),
                favoritePage.getSize(),
                favoritePage.getTotalElements(),
                favoritePage.getTotalPages(),
                favoritePage.isLast()
        );
    }
    
    public Boolean isFavorite(Long recipeId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return favoriteRepository.existsByUser_IdAndRecipe_Id(userDetails.getId(), recipeId);
    }
}