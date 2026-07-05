package com.holosumary.holosummary.repository;

import com.holosumary.holosummary.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {
    Optional<Channel> getChannelByChannelId(String channelId);
}
