package com.holosumary.holosummary.service;

import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Channel;
import com.holosumary.holosummary.model.User;
import com.holosumary.holosummary.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final ChannelService channelService;

    public UserService(UserRepository userRepository,
                       ChannelService channelService) {
        this.userRepository = userRepository;
        this.channelService = channelService;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void addFavoriteChannel(Integer userId, String channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel not found: " + channelId);
        }

        var favourites = user.getFavoriteChannels();
        if (favourites.contains(channel)) {
            return;
        }
        favourites.add(channel);
        userRepository.save(user);
    }

    public void removeFavoriteChannel(Integer userId, String channelId) {
        User user = userRepository.findById(userId).orElse(null);
        Channel channel = channelService.getChannelById(channelId);

        if (user != null && channel != null) {
            user.getFavoriteChannels().remove(channel);
            userRepository.save(user);
        }
    }
}
