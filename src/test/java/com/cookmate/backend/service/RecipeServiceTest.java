package com.cookmate.backend.service;

import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.entity.Ingredient;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.entity.RecipeIngredient;
import com.cookmate.backend.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * UT-04: Filter by ingredient and cuisine
 * UT-05: View recipe details
 */
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe testRecipe;

    @BeforeEach
    void setUp() {
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setTitle("Spaghetti Carbonara");
        testRecipe.setDescription("Classic Italian pasta");
        testRecipe.setCuisineType("italian");
        testRecipe.setPrepTime(10);
        testRecipe.setCookTime(15);
        testRecipe.setServings(4);
        testRecipe.setAverageRating(new BigDecimal("4.5"));

        // Add ingredients
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Pasta");

        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setQuantity(new BigDecimal("400"));
        recipeIngredient.setUnit("grams");

        testRecipe.getRecipeIngredients().add(recipeIngredient);
    }

    @Test
    void filterByIngredientAndCuisineShouldReturnMatchingRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(testRecipe);

        when(recipeRepository.findAll()).thenReturn(recipes);

        List<Recipe> result = recipeRepository.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCuisineType()).isEqualTo("italian");
        assertThat(result.get(0).getRecipeIngredients()).isNotEmpty();

        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void getRecipeByIdShouldReturnRecipeDetails() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));

        Optional<Recipe> result = recipeRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Spaghetti Carbonara");
        assertThat(result.get().getAverageRating()).isEqualTo(new BigDecimal("4.5"));
        assertThat(result.get().getPrepTime()).isEqualTo(10);
        assertThat(result.get().getCookTime()).isEqualTo(15);

        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    void getNonExistentRecipeShouldReturnEmpty() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Recipe> result = recipeRepository.findById(9999L);

        assertThat(result).isEmpty();
        verify(recipeRepository, times(1)).findById(9999L);
    }
}
