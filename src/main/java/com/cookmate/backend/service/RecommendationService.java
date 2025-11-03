package com.cookmate.backend.service;

import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.entity.Favorite;
import com.cookmate.backend.entity.RecentlyViewed;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.repository.FavoriteRepository;
import com.cookmate.backend.repository.RecentlyViewedRepository;
import com.cookmate.backend.repository.RecipeRepository;
import com.cookmate.backend.repository.ReviewRepository;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private RecentlyViewedRepository recentlyViewedRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RecipeService recipeService;

    /**
     * Get personalized recommendations based on type.
     * Supports: personalized, history, preferences, trending
     */
    @Transactional(readOnly = true)
    public PageResponse<RecipeDto> getRecommendations(String type, Authentication authentication, int limit) {
        switch (type != null ? type.toLowerCase() : "personalized") {
            case "history":
                return getRecommendationsByHistory(authentication, limit);
            case "preferences":
                return getRecommendationsByPreferences(authentication, Collections.emptyMap(), limit);
            case "trending":
                return getTrendingRecipes(limit);
            case "personalized":
            default:
                return getPersonalizedRecommendations(authentication, limit);
        }
    }

    /**
     * Personalized recommendations: combines history, favorites, and top-rated recipes
     */
    @Transactional(readOnly = true)
    public PageResponse<RecipeDto> getPersonalizedRecommendations(Authentication authentication, int limit) {
        List<Recipe> coll = new ArrayList<>();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
            int fetch = Math.max(limit / 2, 5);

            // Get recently viewed recipes
            List<RecentlyViewed> rv = recentlyViewedRepository
                    .findByUser_IdOrderByViewedAtDesc(user.getId(), PageRequest.of(0, fetch))
                    .getContent();
            for (RecentlyViewed r : rv) {
                if (r.getRecipe() != null) coll.add(r.getRecipe());
            }

            // Get favorite recipes
            List<Favorite> favs = favoriteRepository
                    .findByUser_Id(user.getId(), PageRequest.of(0, fetch))
                    .getContent();
            for (Favorite f : favs) {
                if (f.getRecipe() != null) coll.add(f.getRecipe());
            }
        }

        // Fill with top-rated recipes
        int need = Math.max(limit, 20);
        List<Recipe> top = recipeRepository
                .findByOrderByAverageRatingDesc(PageRequest.of(0, need))
                .getContent();
        coll.addAll(top);

        // Dedupe preserving first-seen order
        Set<Long> seen = new LinkedHashSet<>();
        List<Recipe> deduped = coll.stream()
                .filter(r -> r != null && r.getId() != null)
                .filter(r -> seen.add(r.getId()))
                .limit(limit)
                .collect(Collectors.toList());

        List<RecipeDto> content = deduped.stream()
                .map(recipeService::convertToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(content, 0, limit, (long) content.size(), 1, true);
    }

    /**
     * Recommendations based on user's viewing history
     */
    @Transactional(readOnly = true)
    public PageResponse<RecipeDto> getRecommendationsByHistory(Authentication authentication, int limit) {
        List<Recipe> recipes = new ArrayList<>();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

            // Get recently viewed recipes
            List<RecentlyViewed> rv = recentlyViewedRepository
                    .findByUser_IdOrderByViewedAtDesc(user.getId(), PageRequest.of(0, limit * 2))
                    .getContent();

            // Extract unique recipes from viewing history
            Set<Long> seen = new LinkedHashSet<>();
            for (RecentlyViewed r : rv) {
                if (r.getRecipe() != null && seen.add(r.getRecipe().getId())) {
                    recipes.add(r.getRecipe());
                    if (recipes.size() >= limit) break;
                }
            }

            // If not enough from history, fill with similar recipes based on viewed cuisines/meal types
            if (recipes.size() < limit) {
                Set<String> viewedCuisines = recipes.stream()
                        .map(Recipe::getCuisineType)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                Set<String> viewedMealTypes = recipes.stream()
                        .map(Recipe::getMealType)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                // Find similar recipes
                List<Recipe> similar = recipeRepository.findAll()
                        .stream()
                        .filter(r -> r.getId() != null && !seen.contains(r.getId()))
                        .filter(r -> {
                            boolean matchCuisine = viewedCuisines.isEmpty() || 
                                    (r.getCuisineType() != null && viewedCuisines.contains(r.getCuisineType()));
                            boolean matchMealType = viewedMealTypes.isEmpty() || 
                                    (r.getMealType() != null && viewedMealTypes.contains(r.getMealType()));
                            return matchCuisine || matchMealType;
                        })
                        .sorted(Comparator.comparing(Recipe::getAverageRating).reversed())
                        .limit(limit - recipes.size())
                        .collect(Collectors.toList());

                recipes.addAll(similar);
            }
        }

        // Fallback to top-rated if no history or not authenticated
        if (recipes.isEmpty()) {
            recipes = recipeRepository
                    .findByOrderByAverageRatingDesc(PageRequest.of(0, limit))
                    .getContent();
        }

        List<RecipeDto> content = recipes.stream()
                .limit(limit)
                .map(recipeService::convertToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(content, 0, limit, (long) content.size(), 1, true);
    }

    /**
     * Recommendations based on user preferences (cuisine, meal type, dietary restrictions)
     */
    @Transactional(readOnly = true)
    public PageResponse<RecipeDto> getRecommendationsByPreferences(
            Authentication authentication, Map<String, Object> preferences, int limit) {
        
        List<Recipe> recipes = new ArrayList<>();
        Set<Long> excludeIds = new HashSet<>();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

            // Exclude recipes user has already viewed or favorited
            List<RecentlyViewed> rv = recentlyViewedRepository
                    .findByUser_IdOrderByViewedAtDesc(user.getId(), PageRequest.of(0, 50))
                    .getContent();
            rv.forEach(r -> {
                if (r.getRecipe() != null) excludeIds.add(r.getRecipe().getId());
            });

            List<Favorite> favs = favoriteRepository
                    .findByUser_Id(user.getId(), PageRequest.of(0, 50))
                    .getContent();
            favs.forEach(f -> {
                if (f.getRecipe() != null) excludeIds.add(f.getRecipe().getId());
            });
        }

        // Extract preferences
        String cuisineType = preferences != null ? (String) preferences.get("cuisineType") : null;
        String mealType = preferences != null ? (String) preferences.get("mealType") : null;
        Boolean isVegetarian = preferences != null ? (Boolean) preferences.get("isVegetarian") : null;
        Boolean isVegan = preferences != null ? (Boolean) preferences.get("isVegan") : null;
        Boolean isGlutenFree = preferences != null ? (Boolean) preferences.get("isGlutenFree") : null;

        // Find recipes matching preferences
        recipes = recipeRepository.findAll()
                .stream()
                .filter(r -> r.getId() != null && !excludeIds.contains(r.getId()))
                .filter(r -> cuisineType == null || (r.getCuisineType() != null && 
                        r.getCuisineType().equalsIgnoreCase(cuisineType)))
                .filter(r -> mealType == null || (r.getMealType() != null && 
                        r.getMealType().equalsIgnoreCase(mealType)))
                .filter(r -> isVegetarian == null || (r.getIsVegetarian() != null && 
                        r.getIsVegetarian().equals(isVegetarian)))
                .filter(r -> isVegan == null || (r.getIsVegan() != null && 
                        r.getIsVegan().equals(isVegan)))
                .filter(r -> isGlutenFree == null || (r.getIsGlutenFree() != null && 
                        r.getIsGlutenFree().equals(isGlutenFree)))
                .sorted(Comparator.comparing(Recipe::getAverageRating).reversed()
                        .thenComparing(Recipe::getViewCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // Fallback to top-rated if no matches
        if (recipes.isEmpty()) {
            recipes = recipeRepository
                    .findByOrderByAverageRatingDesc(PageRequest.of(0, limit))
                    .getContent()
                    .stream()
                    .filter(r -> !excludeIds.contains(r.getId()))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        List<RecipeDto> content = recipes.stream()
                .map(recipeService::convertToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(content, 0, limit, (long) content.size(), 1, true);
    }

    /**
     * Trending recipes based on recent views, favorites, and ratings
     */
    @Transactional(readOnly = true)
    public PageResponse<RecipeDto> getTrendingRecipes(int limit) {
        // Calculate trending score: combination of ratings, reviews, views, and recency
        List<Recipe> recipes = recipeRepository.findAll()
                .stream()
                .filter(r -> r.getId() != null)
                .sorted((r1, r2) -> {
                    // Calculate trending score
                    double score1 = calculateTrendingScore(r1);
                    double score2 = calculateTrendingScore(r2);
                    return Double.compare(score2, score1); // Descending order
                })
                .limit(limit)
                .collect(Collectors.toList());

        List<RecipeDto> content = recipes.stream()
                .map(recipeService::convertToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(content, 0, limit, (long) content.size(), 1, true);
    }

    /**
     * Calculate trending score for a recipe
     * Formula: (averageRating * totalReviews * viewCount) / daysSinceCreation
     */
    private double calculateTrendingScore(Recipe recipe) {
        if (recipe == null) return 0.0;

        double rating = recipe.getAverageRating() != null ? 
                recipe.getAverageRating().doubleValue() : 0.0;
        int reviews = recipe.getTotalReviews() != null ? recipe.getTotalReviews() : 0;
        int views = recipe.getViewCount() != null ? recipe.getViewCount() : 0;

        // Calculate days since creation
        long daysSinceCreation = 1;
        if (recipe.getCreatedAt() != null) {
            daysSinceCreation = Math.max(1, 
                    java.time.temporal.ChronoUnit.DAYS.between(
                            recipe.getCreatedAt(), 
                            java.time.LocalDateTime.now()));
        }

        // Trending score: higher is better
        // Weight recent recipes more heavily
        double recencyWeight = Math.max(1.0, 30.0 / daysSinceCreation);
        
        return (rating * reviews * views * recencyWeight) / daysSinceCreation;
    }

    /**
     * Legacy method for backward compatibility
     */
    @Transactional(readOnly = true)
    public PageResponse<RecipeDto> recommendRecipes(Authentication authentication, int page, int size) {
        return getPersonalizedRecommendations(authentication, size);
    }
}
