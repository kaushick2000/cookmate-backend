package com.cookmate.backend.controller;

import com.cookmate.backend.dto.AIChatRequest;
import com.cookmate.backend.dto.AIChatResponse;
import com.cookmate.backend.service.AIChatbotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AIChatController {

    @Autowired
    private AIChatbotService aiChatbotService;

    /**
     * Get recipe suggestions based on ingredients
     */
    @PostMapping("/recipe-suggestions")
    public ResponseEntity<AIChatResponse> getRecipeSuggestions(@Valid @RequestBody AIChatRequest request) {
        AIChatResponse response = aiChatbotService.getRecipeSuggestions(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Handle general cooking-related chat
     */
    @PostMapping("/general-chat")
    public ResponseEntity<AIChatResponse> handleGeneralChat(@RequestBody AIChatRequest request) {
        AIChatResponse response = aiChatbotService.handleGeneralChat(request.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * Get recipe suggestions with simple ingredient list (for quick queries)
     */
    @GetMapping("/quick-suggestions")
    public ResponseEntity<AIChatResponse> getQuickSuggestions(
            @RequestParam String ingredients,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) String dietary) {
        
        AIChatRequest request = new AIChatRequest();
        request.setIngredients(java.util.Arrays.asList(ingredients.split(",")));
        request.setMealType(mealType);
        if (dietary != null) {
            request.setDietaryRestrictions(java.util.Arrays.asList(dietary.split(",")));
        }
        request.setChatType("recipe_suggestion");
        
        AIChatResponse response = aiChatbotService.getRecipeSuggestions(request);
        return ResponseEntity.ok(response);
    }
}