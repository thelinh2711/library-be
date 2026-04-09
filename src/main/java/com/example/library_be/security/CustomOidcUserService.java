package com.example.library_be.security;

import com.example.library_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        if (email == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"), "Không lấy được email");
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OAuth2AuthenticationException(
                        new OAuth2Error("user_not_registered"),
                        "Email này chưa được đăng ký trong hệ thống"
                ));

        if (!user.getIsActive()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("user_inactive"), "Tài khoản đã bị vô hiệu hóa");
        }

        return new CustomUserDetails(user, oidcUser.getAttributes(),
                oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}