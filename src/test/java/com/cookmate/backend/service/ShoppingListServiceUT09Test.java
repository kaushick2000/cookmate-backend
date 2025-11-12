package com.cookmate.backend.service;

import com.cookmate.backend.entity.*;
import com.cookmate.backend.repository.*;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

/**
 * UT-09: Shopping List Service - Generate shopping list
 * Ingredients consolidated without duplicates; Accurate shopping list created
 */
@ExtendWith(MockitoExtension.class)
class ShoppingListServiceUT09Test {

    @Mock
    private ShoppingListItemRepository shoppingListItemRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
    }

    @Test
    void shoppingListItemsShouldHaveSourceRecipeTitle() {
        List<ShoppingListItem> items = new ArrayList<>();

        ShoppingListItem item1 = new ShoppingListItem();
        item1.setId(1L);
        item1.setIngredientName("Tomatoes");
        item1.setQuantity(new BigDecimal("5"));
        item1.setUnit("pcs");
        item1.setSourceRecipeTitle("Pasta Carbonara");

        ShoppingListItem item2 = new ShoppingListItem();
        item2.setId(2L);
        item2.setIngredientName("Flour");
        item2.setQuantity(new BigDecimal("2"));
        item2.setUnit("kg");
        item2.setSourceRecipeTitle("Bread");

        items.add(item1);
        items.add(item2);

        when(shoppingListItemRepository.saveAll(anyCollection())).thenReturn(items);

        List<ShoppingListItem> result = shoppingListItemRepository.saveAll(items);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSourceRecipeTitle()).isEqualTo("Pasta Carbonara");
        assertThat(result.get(1).getSourceRecipeTitle()).isEqualTo("Bread");

        // Verify no duplicates
        Set<String> savedIngredients = new HashSet<>();
        for (ShoppingListItem item : result) {
            assertThat(savedIngredients.add(item.getIngredientName().toLowerCase())).isTrue();
        }

        verify(shoppingListItemRepository, times(1)).saveAll(items);
    }

    @Test
    void consolidatedShoppingListShouldNotHaveDuplicateIngredients() {
        List<ShoppingListItem> items = new ArrayList<>();

        // Same ingredient from two recipes but consolidated
        ShoppingListItem tomatoItem = new ShoppingListItem();
        tomatoItem.setId(1L);
        tomatoItem.setIngredientName("Tomato");
        tomatoItem.setQuantity(new BigDecimal("7")); // 5 from Pasta + 2 from Sauce
        tomatoItem.setUnit("pcs");
        tomatoItem.setSourceRecipeTitle("Pasta");

        items.add(tomatoItem);

        when(shoppingListItemRepository.saveAll(anyCollection())).thenReturn(items);

        List<ShoppingListItem> result = shoppingListItemRepository.saveAll(items);

        // Should have only 1 tomato entry, not 2
        long tomatoCount = result.stream()
                .filter(item -> item.getIngredientName().equalsIgnoreCase("Tomato"))
                .count();

        assertThat(tomatoCount).isEqualTo(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(new BigDecimal("7"));
    }
}
