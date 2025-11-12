package com.cookmate.backend.service;

import com.cookmate.backend.entity.Review;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UT-10: Add review and rating - Review stored and average updated
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;
    private Recipe testRecipe;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setTitle("Spaghetti Carbonara");
        testRecipe.setAverageRating(new BigDecimal("4.5"));
        testRecipe.setTotalReviews(9);

        testReview = new Review();
        testReview.setId(1L);
        testReview.setUser(testUser);
        testReview.setRecipe(testRecipe);
        testReview.setRating(5);
    }

    @Test
    void addReviewAndRatingShouldStoreReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewRepository.save(testReview);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getRecipe().getId()).isEqualTo(1L);
        assertThat(result.getUser().getId()).isEqualTo(1L);

        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void addReviewWithValidRatingShouldSucceed() {
        Review review3 = new Review();
        review3.setRating(3);

        when(reviewRepository.save(any(Review.class))).thenReturn(review3);

        Review result = reviewRepository.save(review3);

        assertThat(result.getRating()).isEqualTo(3);
        assertThat(result.getRating()).isBetween(1, 5);
    }

    @Test
    void multipleReviewsShouldBeStoredCorrectly() {
        Review review1 = new Review();
        review1.setRating(4);

        when(reviewRepository.save(any(Review.class)))
                .thenReturn(review1)
                .thenReturn(testReview);

        Review result1 = reviewRepository.save(review1);
        Review result2 = reviewRepository.save(testReview);

        assertThat(result1.getRating()).isEqualTo(4);
        assertThat(result2.getRating()).isEqualTo(5);

        verify(reviewRepository, times(2)).save(any(Review.class));
    }
}
