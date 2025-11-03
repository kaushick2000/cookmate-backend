package com.cookmate.backend.controller;

import com.cookmate.backend.dto.ApiResponse;
import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.ReviewDto;
import com.cookmate.backend.dto.ReviewRequest;
import com.cookmate.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping("/recipe/{recipeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDto> createReview(
            @PathVariable Long recipeId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        ReviewDto review = reviewService.createReview(recipeId, request, authentication);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }
    
    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        ReviewDto review = reviewService.updateReview(reviewId, request, authentication);
        return ResponseEntity.ok(review);
    }
    
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        ApiResponse response = reviewService.deleteReview(reviewId, authentication);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<PageResponse<ReviewDto>> getRecipeReviews(
            @PathVariable Long recipeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ReviewDto> reviews = reviewService.getRecipeReviews(recipeId, page, size);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<ReviewDto>> getUserReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ReviewDto> reviews = reviewService.getUserReviews(authentication, page, size);
        return ResponseEntity.ok(reviews);
    }
}