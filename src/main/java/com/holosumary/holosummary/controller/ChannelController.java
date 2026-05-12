package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Channel;
import com.holosumary.holosummary.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/channels")
    public ResponseEntity<List<Channel>> getChannelList() {
        return ResponseEntity.ok().body(channelService.getChannelList());
    }
}
