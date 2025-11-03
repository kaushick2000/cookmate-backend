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
    
    public ShoppingListDto getShoppingListById(Long id, Authentication authentication) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", id));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        if (!shoppingList.getUser().getId().equals(userDetails.getId())) {
            throw new UnauthorizedException("You don't have permission to view this shopping list");
        }
        
        return convertToDto(shoppingList);
    }
    
    public PageResponse<ShoppingListDto> getUserShoppingLists(Authentication authentication, int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ShoppingList> listPage = shoppingListRepository.findByUser_Id(userDetails.getId(), pageable);
        
        List<ShoppingListDto> content = listPage.getContent().stream()
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
            
            shoppingListItemRepository.save(item);
        }
    }
    
    private ShoppingListDto convertToDto(ShoppingList shoppingList) {
        ShoppingListDto dto = new ShoppingListDto();
        dto.setId(shoppingList.getId());
        dto.setUserId(shoppingList.getUser().getId());
        dto.setName(shoppingList.getName());
        dto.setCreatedAt(shoppingList.getCreatedAt());
        
        List<ShoppingListItemDto> items = shoppingList.getItems().stream()
                .map(this::convertToItemDto)
                .collect(Collectors.toList());
        dto.setItems(items);
        
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
        return dto;
    }
}