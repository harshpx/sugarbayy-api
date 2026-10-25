package com.store.sugarbayy.configs.security.oauth;

import lombok.NonNull;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record OAuthUserInfoGoogle(@NonNull OAuth2User user) implements OAuthUserInfo {
    @Override
    public String email() {
        return user.getAttribute("email");
    }

    @Override
    public String name() {
        return user.getAttribute("name");
    }
}
