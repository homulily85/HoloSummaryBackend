package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Channel;
import com.holosumary.holosummary.service.ChannelService;
import com.holosumary.holosummary.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final UserService userService;

    @GetMapping("/channels")
    public ResponseEntity<List<Channel>> getChannelList(Authentication authentication,
                                                        @RequestParam(
                                                                value = "favourite",
                                                                defaultValue = "false")
                                                        String favourite) {
        boolean isFavourite = Boolean.parseBoolean(favourite);
        if (!isFavourite) {
            return ResponseEntity.ok().body(channelService.getChannelList());
        }

        Integer userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userId =
                    Integer.valueOf(Objects.requireNonNull(authentication.getPrincipal()).toString());
        }

        return userId == null ? ResponseEntity.ok().body(null) :
                ResponseEntity.ok().body(userService.findUserById(userId).getFavoriteChannels());
    }
}