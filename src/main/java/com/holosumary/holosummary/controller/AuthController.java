package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {

        if (refreshToken != null && jwtService.isTokenValid(refreshToken)) {
            String email = jwtService.extractEmail(refreshToken);

            String pictureUrl = jwtService.extractPicture(refreshToken);

            String newAccessToken = jwtService.generateAccessToken(email);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", newAccessToken);
            if (pictureUrl != null) {
                responseBody.put("pictureUrl", pictureUrl);
            }

            return ResponseEntity.ok(responseBody);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing refresh token");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);

        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/api/auth/refresh");

        refreshTokenCookie.setMaxAge(0);

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}