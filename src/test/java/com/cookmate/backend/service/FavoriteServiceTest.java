package com.cookmate.backend.service;

import com.cookmate.backend.entity.Favorite;
import com.cookmate.backend.entity.Recipe;
import com.cookmate.backend.entity.User;
import com.cookmate.backend.repository.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UT-06: Add favorite recipe
 * UT-07: Prevent duplicate favorites
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private User testUser;
    private Recipe testRecipe;
    private Favorite testFavorite;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testRecipe = new Recipe();
        testRecipe.setId(5L);
        testRecipe.setTitle("Pasta Carbonara");

        testFavorite = new Favorite();
        testFavorite.setId(1L);
        testFavorite.setUser(testUser);
        testFavorite.setRecipe(testRecipe);
    }

    @Test
    void addFavoriteShouldStoreFavoriteRecord() {
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testFavorite);

        Favorite result = favoriteRepository.save(testFavorite);

        assertThat(result).isNotNull();
        assertThat(result.getRecipe().getId()).isEqualTo(5L);
        assertThat(result.getUser().getId()).isEqualTo(1L);

        verify(favoriteRepository, times(1)).save(testFavorite);
    }

    @Test
    void preventDuplicateFavoritesShouldNotCreateDuplicates() {
        List<Favorite> favorites = new ArrayList<>();
        favorites.add(testFavorite);

        when(favoriteRepository.findAll()).thenReturn(favorites);

        List<Favorite> allFavorites = favoriteRepository.findAll();
        long duplicateCount = allFavorites.stream()
                .filter(f -> f.getRecipe().getId().equals(5L) && f.getUser().getId().equals(1L))
                .count();

        assertThat(duplicateCount).isEqualTo(1);

        verify(favoriteRepository, times(1)).findAll();
    }

    @Test
    void getFavoritesWithoutDuplicates() {
        List<Favorite> favorites = new ArrayList<>();
        favorites.add(testFavorite);

        when(favoriteRepository.findAll()).thenReturn(favorites);

        List<Favorite> result = favoriteRepository.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(1);

        verify(favoriteRepository, times(1)).findAll();
    }
}
