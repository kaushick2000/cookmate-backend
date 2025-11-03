package com.cookmate.backend.controller;

import com.cookmate.backend.dto.IngredientSubstitutionDto;
import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.service.IngredientSubstitutionService;
import com.cookmate.backend.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AIController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AIControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private IngredientSubstitutionService substitutionService;

    @Test
    void postSubstitutions_returnsDto() throws Exception {
        IngredientSubstitutionDto.Substitution sub = new IngredientSubstitutionDto.Substitution("Margarine", "1:1", "Direct replacement");
        IngredientSubstitutionDto dto = new IngredientSubstitutionDto(
            Collections.singletonList(sub),
            "rule-based"
        );

        when(substitutionService.suggest(any(String.class), any(Boolean.class))).thenReturn(dto);

        mvc.perform(post("/api/ai/substitutions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ingredient\":\"butter\",\"useAI\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("rule-based"))
                .andExpect(jsonPath("$.substitutions[0].ingredient").value("Margarine"));
    }

    @Test
    void getRecommendations_returnsPage() throws Exception {
        PageResponse<RecipeDto> page = new PageResponse<>(Collections.emptyList(), 0, 12, 0L, 1, true);
        when(recommendationService.recommendRecipes(any(), any(Integer.class), any(Integer.class))).thenReturn(page);

        mvc.perform(get("/api/ai/recommendations?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0));
    }
}
