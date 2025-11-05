package com.cookmate.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2 Configuration that is only enabled when proper credentials are provided.
 * This prevents 401 errors when OAuth2 credentials are not configured.
 */
@Configuration
@ConditionalOnProperty(
    value = "spring.security.oauth2.client.registration.google.client-id",
    havingValue = "REPLACE_WITH_YOUR_GOOGLE_CLIENT_ID",
    matchIfMissing = false
)
public class OAuth2Config {
    
    /**
     * This configuration is disabled by default until proper Google OAuth2 credentials are provided.
     * 
     * To enable OAuth2 login:
     * 1. Go to Google Cloud Console: https://console.cloud.google.com/apis/credentials
     * 2. Create a new OAuth 2.0 Client ID
     * 3. Set authorized redirect URIs to: http://localhost:8080/oauth2/callback/google
     * 4. Replace the client-id and client-secret in application.properties
     * 
     * Current status: DISABLED (prevents 401 errors)
     */
}