package com.cookmate.backend.service;

import com.cookmate.backend.dto.ApiResponse;
import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.ReviewDto;
import com.cookmate.backend.dto.ReviewRequest;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.entity.Review;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.exception.BadRequestException;
import com.cookmate.backend.exception.ResourceNotFoundException;
import com.cookmate.backend.exception.UnauthorizedException;
import com.cookmate.backend.repository.RecipeRepository;
import com.cookmate.backend.repository.ReviewRepository;
import com.cookmate.backend.repository.UserRepository;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public ReviewDto createReview(Long recipeId, ReviewRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
        
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));
        
        if (reviewRepository.existsByUser_IdAndRecipe_Id(user.getId(), recipeId)) {
            throw new BadRequestException("You have already reviewed this recipe");
        }
        
        Review review = new Review();
        review.setRecipe(recipe);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update recipe average rating
        updateRecipeRating(recipe);
        
        return convertToDto(savedReview);
    }
    
    @Transactional
    public ReviewDto updateReview(Long reviewId, ReviewRequest request, Authentication authentication) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!review.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to update this review");
        }
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review updatedReview = reviewRepository.save(review);
        
        // Update recipe average rating
        updateRecipeRating(review.getRecipe());
        
        return convertToDto(updatedReview);
    }
    
    @Transactional
    public ApiResponse deleteReview(Long reviewId, Authentication authentication) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!review.getUser().getId().equals(userDetails.getId()) &&
            !userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new UnauthorizedException("You don't have permission to delete this review");
        }
        
        Recipe recipe = review.getRecipe();
        reviewRepository.delete(review);
        
        // Update recipe average rating
        updateRecipeRating(recipe);
        
        return new ApiResponse(true, "Review deleted successfully");
    }
    
    public PageResponse<ReviewDto> getRecipeReviews(Long recipeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByRecipe_Id(recipeId, pageable);
        
        List<ReviewDto> content = reviewPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }
    
    public PageResponse<ReviewDto> getUserReviews(Authentication authentication, int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Review> reviewPage = reviewRepository.findByUser_Id(userDetails.getId(), pageable);
        
        List<ReviewDto> content = reviewPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }
    
    private void updateRecipeRating(Recipe recipe) {
        Double averageRating = reviewRepository.calculateAverageRating(recipe.getId());
        Long totalReviews = reviewRepository.countByRecipe_Id(recipe.getId());
        
        recipe.setAverageRating(averageRating != null ? 
                BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        recipe.setTotalReviews(totalReviews.intValue());
        
        recipeRepository.save(recipe);
    }
    
    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setRecipeId(review.getRecipe().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}