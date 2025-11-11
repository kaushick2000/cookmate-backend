package com.cookmate.backend.service;

import com.cookmate.backend.dto.*;
import com.cookmate.backend.entity.*;
import com.cookmate.backend.exception.ResourceNotFoundException;
import com.cookmate.backend.exception.UnauthorizedException;
import com.cookmate.backend.repository.*;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {
    
    @Autowired
    private ShoppingListRepository shoppingListRepository;
    
    @Autowired
    private ShoppingListItemRepository shoppingListItemRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MealPlanRepository mealPlanRepository;
    
    @Transactional
    public ShoppingListDto createShoppingList(ShoppingListRequest request, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
        
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setUser(user);
        shoppingList.setName(request.getName() != null ? request.getName() : "My Shopping List");
        
        ShoppingList savedList = shoppingListRepository.save(shoppingList);
        
        // Add items from recipes
        if (request.getRecipeIds() != null && !request.getRecipeIds().isEmpty()) {
            addItemsFromRecipes(savedList, request.getRecipeIds());
        }
        
        // Add custom items
        if (request.getCustomItems() != null && !request.getCustomItems().isEmpty()) {
            addCustomItems(savedList, request.getCustomItems());
        }
        
        return convertToDto(savedList);
    }
    
    @Transactional
    public ShoppingListDto updateShoppingList(Long id, ShoppingListRequest request, Authentication authentication) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!shoppingList.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to update this shopping list");
        }
        
        shoppingList.setName(request.getName());
        ShoppingList updatedList = shoppingListRepository.save(shoppingList);
        
        return convertToDto(updatedList);
    }
    
    @Transactional
    public ApiResponse deleteShoppingList(Long id, Authentication authentication) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!shoppingList.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this shopping list");
        }
        
        shoppingListRepository.delete(shoppingList);
        
        return new ApiResponse(true, "Shopping list deleted successfully");
    }
    
    @Transactional(readOnly = true)
    public ShoppingListDto getShoppingListById(Long id, Authentication authentication) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!shoppingList.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to view this shopping list");
        }
        
        // Eagerly load items to avoid LazyInitializationException
        shoppingList.getItems().size();
        
        return convertToDto(shoppingList);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ShoppingListDto> getUserShoppingLists(Authentication authentication, int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ShoppingList> listPage = shoppingListRepository.findByUser_Id(userDetails.getId(), pageable);
        
        // Eagerly load items for all shopping lists to avoid LazyInitializationException
        List<ShoppingList> shoppingLists = listPage.getContent();
        for (ShoppingList shoppingList : shoppingLists) {
            // Initialize the lazy collection within the transaction
            shoppingList.getItems().size();
        }
        
        List<ShoppingListDto> content = shoppingLists.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                listPage.getNumber(),
                listPage.getSize(),
                listPage.getTotalElements(),
                listPage.getTotalPages(),
                listPage.isLast()
        );
    }
    
    @Transactional
    public ShoppingListDto addItemToList(Long listId, ShoppingListItemRequest request, Authentication authentication) {
        ShoppingList shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", listId));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!shoppingList.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to modify this shopping list");
        }
        
        ShoppingListItem item = new ShoppingListItem();
        item.setShoppingList(shoppingList);
        item.setIngredientName(request.getIngredientName());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        item.setCategory(request.getCategory());
        item.setIsPurchased(false);
        
        // Set recipe information if provided
        if (request.getSourceRecipeId() != null) {
            try {
                Recipe recipe = recipeRepository.findById(request.getSourceRecipeId()).orElse(null);
                if (recipe != null) {
                    item.setSourceRecipe(recipe);
                    item.setSourceRecipeTitle(recipe.getTitle());
                }
            } catch (Exception e) {
                // If recipe loading fails, still set the provided title
                item.setSourceRecipeTitle(request.getSourceRecipeTitle());
            }
        } else if (request.getSourceRecipeTitle() != null) {
            item.setSourceRecipeTitle(request.getSourceRecipeTitle());
        }
        
        shoppingListItemRepository.save(item);
        
        return convertToDto(shoppingList);
    }
    
    @Transactional
    public ApiResponse toggleItemPurchased(Long itemId, Authentication authentication) {
        ShoppingListItem item = shoppingListItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingListItem", "id", itemId));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!item.getShoppingList().getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to modify this item");
        }
        
        item.setIsPurchased(!item.getIsPurchased());
        shoppingListItemRepository.save(item);
        
        return new ApiResponse(true, "Item status updated");
    }
    
    @Transactional
    public ApiResponse deleteItem(Long itemId, Authentication authentication) {
        ShoppingListItem item = shoppingListItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingListItem", "id", itemId));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!item.getShoppingList().getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this item");
        }
        
        shoppingListItemRepository.delete(item);
        
        return new ApiResponse(true, "Item deleted successfully");
    }
    
    private void addItemsFromRecipes(ShoppingList shoppingList, List<Long> recipeIds) {
        Map<String, ShoppingListItem> itemMap = new HashMap<>();
        
        for (Long recipeId : recipeIds) {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));
            
            for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
                String ingredientName = recipeIngredient.getIngredient().getName().toLowerCase();
                
                if (itemMap.containsKey(ingredientName)) {
                    // Combine quantities if same ingredient
                    ShoppingListItem existingItem = itemMap.get(ingredientName);
                    if (existingItem.getUnit() != null && 
                        existingItem.getUnit().equals(recipeIngredient.getUnit())) {
                        BigDecimal newQuantity = existingItem.getQuantity()
                                .add(recipeIngredient.getQuantity());
                        existingItem.setQuantity(newQuantity);
                    }
                } else {
                    ShoppingListItem item = new ShoppingListItem();
                    item.setShoppingList(shoppingList);
                    item.setIngredient(recipeIngredient.getIngredient());
                    item.setIngredientName(recipeIngredient.getIngredient().getName());
                    item.setQuantity(recipeIngredient.getQuantity());
                    item.setUnit(recipeIngredient.getUnit());
                    item.setCategory(recipeIngredient.getIngredient().getCategory());
                    item.setIsPurchased(false);
                    item.setSourceRecipe(recipe);
                    item.setSourceRecipeTitle(recipe.getTitle());
                    
                    itemMap.put(ingredientName, item);
                }
            }
        }
        
        shoppingListItemRepository.saveAll(itemMap.values());
    }
    
    private void addCustomItems(ShoppingList shoppingList, List<ShoppingListItemRequest> customItems) {
        for (ShoppingListItemRequest itemRequest : customItems) {
            ShoppingListItem item = new ShoppingListItem();
            item.setShoppingList(shoppingList);
            item.setIngredientName(itemRequest.getIngredientName());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnit(itemRequest.getUnit());
            item.setCategory(itemRequest.getCategory());
            item.setIsPurchased(false);
            
            // Set recipe information if provided
            if (itemRequest.getSourceRecipeId() != null) {
                // Optional: Load the recipe to set both ID and title
                try {
                    Recipe recipe = recipeRepository.findById(itemRequest.getSourceRecipeId()).orElse(null);
                    if (recipe != null) {
                        item.setSourceRecipe(recipe);
                        item.setSourceRecipeTitle(recipe.getTitle());
                    }
                } catch (Exception e) {
                    // If recipe loading fails, still set the provided title
                    item.setSourceRecipeTitle(itemRequest.getSourceRecipeTitle());
                }
            } else if (itemRequest.getSourceRecipeTitle() != null) {
                item.setSourceRecipeTitle(itemRequest.getSourceRecipeTitle());
            }
            
            shoppingListItemRepository.save(item);
        }
    }
    
    private ShoppingListDto convertToDto(ShoppingList shoppingList) {
        ShoppingListDto dto = new ShoppingListDto();
        dto.setId(shoppingList.getId());
        dto.setUserId(shoppingList.getUser().getId());
        dto.setName(shoppingList.getName());
        dto.setCreatedAt(shoppingList.getCreatedAt());
        
        // Safely handle items collection
        try {
            List<ShoppingListItemDto> items = shoppingList.getItems().stream()
                    .map(this::convertToItemDto)
                    .collect(Collectors.toList());
            dto.setItems(items);
        } catch (Exception e) {
            // If collection is not initialized, set empty list
            System.err.println("Warning: items collection not initialized for shopping list " + shoppingList.getId());
            dto.setItems(new ArrayList<>());
        }
        
        return dto;
    }
    
    private ShoppingListItemDto convertToItemDto(ShoppingListItem item) {
        ShoppingListItemDto dto = new ShoppingListItemDto();
        dto.setId(item.getId());
        dto.setIngredientId(item.getIngredient() != null ? item.getIngredient().getId() : null);
        dto.setIngredientName(item.getIngredientName());
        dto.setQuantity(item.getQuantity());
        dto.setUnit(item.getUnit());
        dto.setIsPurchased(item.getIsPurchased());
        dto.setCategory(item.getCategory());
        dto.setSourceRecipeId(item.getSourceRecipe() != null ? item.getSourceRecipe().getId() : null);
        dto.setSourceRecipeTitle(item.getSourceRecipeTitle());
        return dto;
    }
    
    @Transactional
    public ShoppingListDto generateFromMealPlans(Long listId, Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        LocalDate today = LocalDate.now();
        
        // Get active meal plans for the user (where today is between start and end dates)
        List<MealPlan> activeMealPlans = mealPlanRepository
                .findActiveMealPlans(userDetails.getId(), today);
        
        if (activeMealPlans.isEmpty()) {
            throw new ResourceNotFoundException("No active meal plans found");
        }
        
        // Get or create shopping list
        ShoppingList shoppingList;
        if (listId != null) {
            ShoppingList existingList = shoppingListRepository.findById(listId)
                    .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", listId));
            
            if (!existingList.getUser().getId().equals(userDetails.getId())) {
                throw new UnauthorizedException("You don't have permission to modify this shopping list");
            }
            
            // Clear existing items
            existingList.getItems().clear();
            shoppingListItemRepository.deleteByShoppingList_Id(listId);
            shoppingList = existingList;
        } else {
            // Create new shopping list
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getId()));
            
            ShoppingList newList = new ShoppingList();
            newList.setUser(user);
            newList.setName("Shopping List from Meal Plans");
            shoppingList = shoppingListRepository.save(newList);
        }
        
        // Collect all recipes from meal plans with their servings
        Map<Long, Integer> recipeServingsMap = new HashMap<>();
        for (MealPlan mealPlan : activeMealPlans) {
            // Eagerly load mealPlanRecipes
            mealPlan.getMealPlanRecipes().size();
            
            for (MealPlanRecipe mealPlanRecipe : mealPlan.getMealPlanRecipes()) {
                Long recipeId = mealPlanRecipe.getRecipe().getId();
                Integer servings = mealPlanRecipe.getServings() != null ? mealPlanRecipe.getServings() : 1;
                
                // Accumulate servings for the same recipe
                recipeServingsMap.put(recipeId, 
                    recipeServingsMap.getOrDefault(recipeId, 0) + servings);
            }
        }
        
        // Aggregate ingredients from all recipes
        Map<String, ShoppingListItem> itemMap = new HashMap<>();
        
        for (Map.Entry<Long, Integer> entry : recipeServingsMap.entrySet()) {
            Long recipeId = entry.getKey();
            Integer totalServings = entry.getValue();
            
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", recipeId));
            
            // Eagerly load recipe ingredients
            recipe.getRecipeIngredients().size();
            
            Integer recipeServings = recipe.getServings() != null ? recipe.getServings() : 1;
            BigDecimal servingMultiplier = BigDecimal.valueOf(totalServings)
                    .divide(BigDecimal.valueOf(recipeServings), 2, RoundingMode.HALF_UP);
            
            for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
                String ingredientName = recipeIngredient.getIngredient().getName().toLowerCase();
                String unit = recipeIngredient.getUnit();
                
                // Calculate adjusted quantity based on servings
                BigDecimal adjustedQuantity = recipeIngredient.getQuantity() != null 
                    ? recipeIngredient.getQuantity().multiply(servingMultiplier)
                    : BigDecimal.ZERO;
                
                String key = ingredientName + "|" + (unit != null ? unit : "");
                
                if (itemMap.containsKey(key)) {
                    // Combine quantities if same ingredient and unit
                    ShoppingListItem existingItem = itemMap.get(key);
                    BigDecimal newQuantity = existingItem.getQuantity()
                            .add(adjustedQuantity);
                    existingItem.setQuantity(newQuantity);
                } else {
                    ShoppingListItem item = new ShoppingListItem();
                    item.setShoppingList(shoppingList);
                    item.setIngredient(recipeIngredient.getIngredient());
                    item.setIngredientName(recipeIngredient.getIngredient().getName());
                    item.setQuantity(adjustedQuantity);
                    item.setUnit(unit);
                    item.setCategory(recipeIngredient.getIngredient().getCategory());
                    item.setIsPurchased(false);
                    
                    itemMap.put(key, item);
                }
            }
        }
        
        // Save all items
        if (!itemMap.isEmpty()) {
            shoppingListItemRepository.saveAll(itemMap.values());
        }
        
        // Flush to ensure all items are saved
        shoppingListItemRepository.flush();
        
        // Reload shopping list to get updated items
        ShoppingList reloadedList = shoppingListRepository.findById(shoppingList.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", shoppingList.getId()));
        
        // Eagerly load items to avoid LazyInitializationException
        reloadedList.getItems().size();
        
        return convertToDto(reloadedList);
    }
}