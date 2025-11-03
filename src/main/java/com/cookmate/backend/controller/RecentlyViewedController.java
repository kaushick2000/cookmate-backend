package com.cookmate.backend.controller;

import com.cookmate.backend.dto.PageResponse;
import com.cookmate.backend.dto.RecipeDto;
import com.cookmate.backend.service.RecentlyViewedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recently-viewed")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("isAuthenticated()")
public class RecentlyViewedController {
    
    @Autowired
    private RecentlyViewedService recentlyViewedService;
    
    @GetMapping
    public ResponseEntity<PageResponse<RecipeDto>> getRecentlyViewed(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        PageResponse<RecipeDto> recipes = recentlyViewedService.getRecentlyViewed(authentication, page, size);
        return ResponseEntity.ok(recipes);
    }
}