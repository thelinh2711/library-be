package com.example.library_be.security;

import com.example.library_be.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails, OidcUser {

    private final User user;
    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    // Login thường
    public CustomUserDetails(User user) {
        this.user = user;
        this.attributes = Map.of();
        this.idToken = null;
        this.userInfo = null;
    }

    // Google login (OIDC)
    public CustomUserDetails(User user, Map<String, Object> attributes,
                             OidcIdToken idToken, OidcUserInfo userInfo) {
        this.user = user;
        this.attributes = attributes;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    // OidcUser
    @Override public Map<String, Object> getClaims() { return attributes; }
    @Override public OidcUserInfo getUserInfo()       { return userInfo; }
    @Override public OidcIdToken getIdToken()         { return idToken; }

    // OAuth2User
    @Override public Map<String, Object> getAttributes() { return attributes; }
    @Override public String getName()                     { return user.getEmail(); }

    // UserDetails
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
    @Override public String getPassword()              { return user.getPassword(); }
    @Override public String getUsername()              { return user.getEmail(); }
    @Override public boolean isEnabled()               { return user.getIsActive(); }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    public UUID getUserId() { return user.getId(); }
}
