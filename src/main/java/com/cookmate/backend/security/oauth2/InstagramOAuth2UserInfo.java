package com.cookmate.backend.security.oauth2;

import java.util.Map;

public class InstagramOAuth2UserInfo extends OAuth2UserInfo {

    public InstagramOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getFirstName() {
        String username = (String) attributes.get("username");
        return username != null ? username : "";
    }

    @Override
    public String getLastName() {
        return ""; // Instagram doesn't provide separate first/last names
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_picture_url");
    }
}