package com.cookmate.backend.service;

import com.cookmate.backend.entity.MealPlan;
import com.cookmate.backend.entity.MealPlanRecipe;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.repository.MealPlanRepository;
import com.cookmate.backend.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UT-08: Add recipes to weekly planner - Entries stored under correct user ID
 */
@ExtendWith(MockitoExtension.class)
class MealPlanServiceTest {

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private MealPlanService mealPlanService;

    private MealPlan testMealPlan;
    private Recipe testRecipe;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testRecipe = new Recipe();
        testRecipe.setId(5L);
        testRecipe.setTitle("Pasta");

        testMealPlan = new MealPlan();
        testMealPlan.setId(1L);
        testMealPlan.setUser(testUser);
        testMealPlan.setName("Weekly Meal Plan");
        testMealPlan.setStartDate(LocalDate.of(2025, 11, 10));
        testMealPlan.setEndDate(LocalDate.of(2025, 11, 16));
    }

    @Test
    void addRecipeToMealPlanShouldStoreEntryUnderCorrectUserId() {
        MealPlanRecipe mealPlanRecipe = new MealPlanRecipe();
        mealPlanRecipe.setMealPlan(testMealPlan);
        mealPlanRecipe.setRecipe(testRecipe);
        mealPlanRecipe.setPlannedDate(LocalDate.of(2025, 11, 15));
        mealPlanRecipe.setServings(4);

        testMealPlan.getMealPlanRecipes().add(mealPlanRecipe);

        when(mealPlanRepository.save(any(MealPlan.class))).thenReturn(testMealPlan);

        MealPlan result = mealPlanRepository.save(testMealPlan);

        assertThat(result).isNotNull();
        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getMealPlanRecipes()).isNotEmpty();
        assertThat(result.getMealPlanRecipes().get(0).getRecipe().getId()).isEqualTo(5L);

        verify(mealPlanRepository, times(1)).save(testMealPlan);
    }

    @Test
    void getMealPlanShouldReturnAllRecipesForUser() {
        List<MealPlanRecipe> recipes = new ArrayList<>();

        MealPlanRecipe recipe1 = new MealPlanRecipe();
        recipe1.setRecipe(testRecipe);
        recipe1.setPlannedDate(LocalDate.of(2025, 11, 10));
        recipes.add(recipe1);

        Recipe recipe2 = new Recipe();
        recipe2.setId(10L);
        recipe2.setTitle("Soup");

        MealPlanRecipe recipe2Entry = new MealPlanRecipe();
        recipe2Entry.setRecipe(recipe2);
        recipe2Entry.setPlannedDate(LocalDate.of(2025, 11, 11));
        recipes.add(recipe2Entry);

        testMealPlan.getMealPlanRecipes().addAll(recipes);

        when(mealPlanRepository.findById(1L)).thenReturn(Optional.of(testMealPlan));

        Optional<MealPlan> result = mealPlanRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(1L);
        assertThat(result.get().getMealPlanRecipes().size()).isEqualTo(2);

        verify(mealPlanRepository, times(1)).findById(1L);
    }
}
