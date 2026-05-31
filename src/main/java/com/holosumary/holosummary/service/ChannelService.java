package com.holosumary.holosummary.service;

import com.holosumary.holosummary.dto.channel.ChannelResponseDTO;
import com.holosumary.holosummary.dto.channel.GroupResponseDTO;
import com.holosumary.holosummary.model.Channel;
import com.holosumary.holosummary.model.User;
import com.holosumary.holosummary.repository.ChannelRepository;
import com.holosumary.holosummary.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public List<ChannelResponseDTO> getChannelListWithFavorites(Integer userId) {
        List<Channel> allChannels =
                channelRepository.findAll(Sort.by(Sort.Direction.ASC, "group" +
                        ".orderValue"));

        Set<String> favoriteChannelIds;

        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            favoriteChannelIds =
                    user.map(value -> value.getFavoriteChannels().stream()
                            .map(Channel::getId)
                            .collect(Collectors.toSet())).orElseGet(Set::of);
        } else {
            favoriteChannelIds = Set.of();
        }

        return allChannels.stream().map(channel -> {
            ChannelResponseDTO dto = new ChannelResponseDTO();
            dto.setId(channel.getId());
            dto.setName(channel.getName());
            dto.setEnglishName(channel.getEnglishName());
            dto.setPhoto(channel.getPhoto());
            dto.setGroup(new GroupResponseDTO(channel.getGroup().getName()));
            dto.setFavorite(favoriteChannelIds.contains(channel.getId()));
            return dto;
        }).collect(Collectors.toList());
    }

    public Channel getChannelById(String id) {
        return channelRepository.findById(id).orElse(null);
    }
}