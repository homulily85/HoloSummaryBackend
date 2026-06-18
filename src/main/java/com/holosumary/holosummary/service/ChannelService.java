package com.holosumary.holosummary.service;

import com.holosumary.holosummary.model.Channel;
import com.holosumary.holosummary.repository.ChannelRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    public List<Channel> getChannelList() {
        return channelRepository.findAll(Sort.by(Sort.Direction.ASC, "group" +
                ".orderValue"));
    }

    public Channel getChannelById(String id) {
        return channelRepository.findById(id).orElse(null);
    }
}