package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController()
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> addFavouriteChannel(
            Authentication authentication,
            @RequestBody Map<String, String> payload) {

        String channelId = payload.get("channelId");

        if (channelId == null || channelId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "channelId is required"));
        }

        Integer userId = Integer.valueOf(Objects.requireNonNull(authentication.getPrincipal()).toString());

        userService.addFavoriteChannel(userId, channelId);
        return ResponseEntity.ok().body(Map.of("message", "Channel added to favorites"));
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<?> removeFavouriteChannel(
            Authentication authentication,
            @RequestBody Map<String, String> payload
    ) {
        String channelId = payload.get("channelId");

        if (channelId == null || channelId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "channelId is required"));
        }

        Integer userId =
                Integer.valueOf(Objects.requireNonNull(authentication.getPrincipal()).toString());
        userService.removeFavoriteChannel(userId, channelId);
        return ResponseEntity.ok().body("Channel deleted from favorites");
    }
}
