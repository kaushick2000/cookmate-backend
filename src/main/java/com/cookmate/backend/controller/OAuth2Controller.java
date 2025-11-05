package com.cookmate.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OAuth2Controller {

    @GetMapping("/login/{provider}")
    public void redirectToOAuth2(@PathVariable String provider, HttpServletResponse response) throws IOException {
        // Check if OAuth2 is properly configured
        if (provider.equals("google")) {
            // For now, return an error message instead of redirecting
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"OAuth2 not configured\",\"message\":\"Please configure OAuth2 client credentials in application.properties\"}");
            return;
        }
        
        String redirectUrl = "/oauth2/authorization/" + provider;
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/status")
    public Map<String, String> getOAuth2Status() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "OAuth2 requires configuration");
        status.put("message", "Please set up Google OAuth2 client ID and secret in application.properties");
        status.put("documentation", "https://developers.google.com/identity/protocols/oauth2");
        return status;
    }
}