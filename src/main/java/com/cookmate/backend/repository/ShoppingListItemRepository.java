package com.cookmate.backend.repository;

import com.cookmate.backend.entity.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
    
    List<ShoppingListItem> findByShoppingList_Id(Long shoppingListId);
    
    List<ShoppingListItem> findByShoppingList_IdAndIsPurchased(Long shoppingListId, Boolean isPurchased);
    
    void deleteByShoppingList_Id(Long shoppingListId);
}