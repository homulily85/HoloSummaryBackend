package com.holosumary.holosummary.security;

import com.holosumary.holosummary.model.User;
import com.holosumary.holosummary.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    @Value("${application.frontend.url}")
    private String redirectUri;

    public OAuth2LoginSuccessHandler(JwtService jwtService,
                                     UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String picture = oAuth2User.getAttribute("picture");

        User user = userService.findUserByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setRole(User.ROLE_USER);
            user = userService.createUser(user);
        }

        String refreshToken =
                jwtService.generateRefreshToken(String.valueOf(user.getId()),
                user.getRole(), picture);

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        refreshTokenCookie.setPath("/api/auth/refresh");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshTokenCookie);

        // Redirect to frontend with ONLY the access token
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}