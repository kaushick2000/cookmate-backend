package com.cookmate.backend.security.oauth2;

import com.cookmate.backend.entity.User;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(User.AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(User.AuthProvider.TWITTER.toString())) {
            return new TwitterOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(User.AuthProvider.INSTAGRAM.toString())) {
            return new InstagramOAuth2UserInfo(attributes);
        } else {
            throw new UnsupportedOperationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}