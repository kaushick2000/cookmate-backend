package com.cookmate.backend.repository;

import com.cookmate.backend.entity.ShoppingList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    
    Page<ShoppingList> findByUser_Id(Long userId, Pageable pageable);
    
    List<ShoppingList> findByUser_IdOrderByCreatedAtDesc(Long userId);
}