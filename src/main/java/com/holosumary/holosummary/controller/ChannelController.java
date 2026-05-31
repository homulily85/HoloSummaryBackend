package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.dto.channel.ChannelResponseDTO;
import com.holosumary.holosummary.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/channels")
    public ResponseEntity<List<ChannelResponseDTO>> getChannelList(Authentication authentication) {
        Integer userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userId =
                    Integer.valueOf(Objects.requireNonNull(authentication.getPrincipal()).toString());
        }

        return ResponseEntity.ok().body(channelService.getChannelListWithFavorites(userId));
    }
}