package com.cookmate.backend.controller;

import com.cookmate.backend.dto.*;
import com.cookmate.backend.service.ShoppingListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-lists")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
public class ShoppingListController {
    
    @Autowired
    private ShoppingListService shoppingListService;
    
    @PostMapping
    public ResponseEntity<ShoppingListDto> createShoppingList(
            @Valid @RequestBody ShoppingListRequest request,
            Authentication authentication) {
        ShoppingListDto shoppingList = shoppingListService.createShoppingList(request, authentication);
        return new ResponseEntity<>(shoppingList, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ShoppingListDto> updateShoppingList(
            @PathVariable Long id,
            @Valid @RequestBody ShoppingListRequest request,
            Authentication authentication) {
        ShoppingListDto shoppingList = shoppingListService.updateShoppingList(id, request, authentication);
        return ResponseEntity.ok(shoppingList);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteShoppingList(
            @PathVariable Long id,
            Authentication authentication) {
        ApiResponse response = shoppingListService.deleteShoppingList(id, authentication);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListDto> getShoppingListById(
            @PathVariable Long id,
            Authentication authentication) {
        ShoppingListDto shoppingList = shoppingListService.getShoppingListById(id, authentication);
        return ResponseEntity.ok(shoppingList);
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<ShoppingListDto>> getUserShoppingLists(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ShoppingListDto> shoppingLists = shoppingListService
                .getUserShoppingLists(authentication, page, size);
        return ResponseEntity.ok(shoppingLists);
    }
    
    @PostMapping("/{listId}/items")
    public ResponseEntity<ShoppingListDto> addItemToList(
            @PathVariable Long listId,
            @Valid @RequestBody ShoppingListItemRequest request,
            Authentication authentication) {
        ShoppingListDto shoppingList = shoppingListService.addItemToList(listId, request, authentication);
        return ResponseEntity.ok(shoppingList);
    }
    
    @PutMapping("/items/{itemId}/toggle")
    public ResponseEntity<ApiResponse> toggleItemPurchased(
            @PathVariable Long itemId,
            Authentication authentication) {
        ApiResponse response = shoppingListService.toggleItemPurchased(itemId, authentication);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse> deleteItem(
            @PathVariable Long itemId,
            Authentication authentication) {
        ApiResponse response = shoppingListService.deleteItem(itemId, authentication);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/generate-from-meal-plans")
    public ResponseEntity<ShoppingListDto> generateFromMealPlans(
            @RequestParam(required = false) Long listId,
            Authentication authentication) {
        ShoppingListDto shoppingList = shoppingListService.generateFromMealPlans(listId, authentication);
        return ResponseEntity.ok(shoppingList);
    }
}