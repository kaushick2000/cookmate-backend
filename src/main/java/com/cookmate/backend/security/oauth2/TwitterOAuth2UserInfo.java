package com.cookmate.backend.security.oauth2;

import java.util.Map;

public class TwitterOAuth2UserInfo extends OAuth2UserInfo {

    public TwitterOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getFirstName() {
        String name = (String) attributes.get("name");
        if (name != null && name.contains(" ")) {
            return name.split(" ")[0];
        }
        return name;
    }

    @Override
    public String getLastName() {
        String name = (String) attributes.get("name");
        if (name != null && name.contains(" ")) {
            String[] parts = name.split(" ");
            if (parts.length > 1) {
                return parts[parts.length - 1];
            }
        }
        return "";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("profile_image_url");
    }
}