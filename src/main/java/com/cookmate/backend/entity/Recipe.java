package com.cookmate.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "cuisine_type", length = 50)
    private String cuisineType;
    
    @Column(name = "meal_type", length = 50)
    private String mealType;
    
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;
    
    @Column(name = "prep_time")
    private Integer prepTime;
    
    @Column(name = "cook_time")
    private Integer cookTime;
    
    @Column(name = "total_time")
    private Integer totalTime;
    
    private Integer servings = 4;
    
    private Integer calories;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal protein;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal carbs;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fat;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fiber;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Lob
    @Column(name = "image_data", columnDefinition = "MEDIUMBLOB")
    private byte[] imageData;
    
    @Column(name = "image_filename")
    private String imageFilename;
    
    @Column(name = "image_content_type")
    private String imageContentType;
    
    @Column(name = "video_url", length = 500)
    private String videoUrl;
    
    @Column(name = "is_vegetarian")
    private Boolean isVegetarian = false;
    
    @Column(name = "is_vegan")
    private Boolean isVegan = false;
    
    @Column(name = "is_gluten_free")
    private Boolean isGlutenFree = false;
    
    @Column(name = "is_dairy_free")
    private Boolean isDairyFree = false;
    
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private List<Instruction> instructions = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorite> favorites = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecentlyViewed> recentlyViewed = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Recommendation> recommendations = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IngredientSubstitution> ingredientSubstitutions = new HashSet<>();
}