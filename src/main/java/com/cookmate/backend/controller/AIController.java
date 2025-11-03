package com.cookmate.backend.controller;

import com.cookmate.backend.dto.IngredientSubstitutionDto;
import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.service.IngredientSubstitutionService;
import com.cookmate.backend.service.RecommendationService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AIController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private IngredientSubstitutionService substitutionService;

    @GetMapping("/recommendations")
    public ResponseEntity<PageResponse<RecipeDto>> recommendations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size) {

        PageResponse<RecipeDto> resp = recommendationService.recommendRecipes(authentication, page, size);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/substitutions")
    public ResponseEntity<IngredientSubstitutionDto> substitutions(@RequestBody Map<String, Object> body) {
        String ingredient = body.containsKey("ingredient") ? body.get("ingredient").toString() : "";
        boolean useAI = body.containsKey("useAI") && Boolean.parseBoolean(body.get("useAI").toString());
        IngredientSubstitutionDto dto = substitutionService.suggest(ingredient, useAI);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/substitutions")
    public ResponseEntity<IngredientSubstitutionDto> getSubstitutions(
            @RequestParam String ingredient,
            @RequestParam(defaultValue = "false") boolean useAI) {
        IngredientSubstitutionDto dto = substitutionService.suggest(ingredient, useAI);
        return ResponseEntity.ok(dto);
    }
}
