package com.cookmate.backend.service;

import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.entity.RecentlyViewed;
import com.cookmate.backend.repository.RecentlyViewedRepository;
import com.cookmate.backend.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecentlyViewedService {
    
    @Autowired
    private RecentlyViewedRepository recentlyViewedRepository;
    
    @Autowired
    private RecipeService recipeService;
    
    public PageResponse<RecipeDto> getRecentlyViewed(Authentication authentication, int page, int size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<RecentlyViewed> recentlyViewedPage = recentlyViewedRepository
                .findByUser_IdOrderByViewedAtDesc(userDetails.getId(), pageable);
        
        List<RecipeDto> content = recentlyViewedPage.getContent().stream()
                .map(viewed -> recipeService.convertToDto(viewed.getRecipe()))
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                recentlyViewedPage.getNumber(),
                recentlyViewedPage.getSize(),
                recentlyViewedPage.getTotalElements(),
                recentlyViewedPage.getTotalPages(),
                recentlyViewedPage.isLast()
        );
    }
}