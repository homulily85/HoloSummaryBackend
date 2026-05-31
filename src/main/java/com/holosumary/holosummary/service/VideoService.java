package com.holosumary.holosummary.service;

import com.holosumary.holosummary.dto.channel.ChannelResponseDTO;
import com.holosumary.holosummary.dto.channel.GroupResponseDTO;
import com.holosumary.holosummary.dto.video.VideoResponseDTO;
import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Channel;
import com.holosumary.holosummary.model.User;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.UserRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    private Set<String> getFavoriteChannelIds(Integer userId) {
        if (userId == null) {
            return Set.of();
        }
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> value.getFavoriteChannels().stream()
                .map(Channel::getId)
                .collect(Collectors.toSet())).orElseGet(Set::of);
    }

    private ChannelResponseDTO mapChannel(Channel channel,
                                          Set<String> favoriteIds) {
        if (channel == null) return null;
        ChannelResponseDTO dto = new ChannelResponseDTO();
        dto.setId(channel.getId());
        dto.setName(channel.getName());
        dto.setEnglishName(channel.getEnglishName());
        dto.setPhoto(channel.getPhoto());
        dto.setGroup(new GroupResponseDTO(channel.getGroup().getName()));
        dto.setFavorite(favoriteIds.contains(channel.getId()));
        return dto;
    }

    private VideoResponseDTO mapToDto(Video video, Set<String> favoriteIds) {
        VideoResponseDTO dto = new VideoResponseDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTitle());
        dto.setType(video.getType());
        dto.setTopic(video.getTopic());
        dto.setDuration(video.getDuration());
        dto.setStatus(video.getStatus());
        dto.setAvailableAt(video.getAvailableAt());

        dto.setChannel(mapChannel(video.getChannel(), favoriteIds));

        if (video.getMentions() != null) {
            dto.setMentions(video.getMentions().stream()
                    .map(c -> mapChannel(c, favoriteIds))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // --- Updated Fetch Methods ---

    public Page<VideoResponseDTO> getVideosByStatus(String status,
                                                    int pageNumber,
                                                    int pageSize,
                                                    String sortBy,
                                                    String sortOrder,
                                                    Integer userId) {
        Set<String> favIds = getFavoriteChannelIds(userId);
        return videoRepository.findByStatus(status, getPageRequest(pageNumber
                        , pageSize, sortBy, sortOrder))
                .map(video -> mapToDto(video, favIds));
    }

    public Page<VideoResponseDTO> getAllVideos(int pageNumber, int pageSize,
                                               String sortBy,
                                               String sortOrder,
                                               Integer userId) {
        Set<String> favIds = getFavoriteChannelIds(userId);
        return videoRepository.findAll(getPageRequest(pageNumber, pageSize,
                        sortBy, sortOrder))
                .map(video -> mapToDto(video, favIds));
    }

    public Page<VideoResponseDTO> getVideosAvailableAfter(OffsetDateTime availableAfter, int pageNumber,
                                                          int pageSize,
                                                          String sortBy,
                                                          String sortOrder,
                                                          Integer userId) {
        Set<String> favIds = getFavoriteChannelIds(userId);
        return videoRepository.findByAvailableAtAfter(availableAfter,
                        getPageRequest(pageNumber, pageSize, sortBy, sortOrder))
                .map(video -> mapToDto(video, favIds));
    }

    public Page<VideoResponseDTO> getVideoByStatusAndAvailableAfter(String status, OffsetDateTime availableAfter,
                                                                    int pageNumber, int pageSize,
                                                                    String sortBy, String sortOrder, Integer userId) {
        Set<String> favIds = getFavoriteChannelIds(userId);
        return videoRepository.findByStatusAndAvailableAtAfter(status,
                        availableAfter, getPageRequest(pageNumber, pageSize,
                                sortBy, sortOrder))
                .map(video -> mapToDto(video, favIds));
    }

    public VideoResponseDTO getVideoById(String id, Integer userId) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Video not found " +
                        "with id: " + id));
        Set<String> favIds = getFavoriteChannelIds(userId);
        return mapToDto(video, favIds);
    }

    private PageRequest getPageRequest(int pageNumber, int pageSize,
                                       String sortBy, String sortOrder) {
        return PageRequest.of(pageNumber, pageSize,
                Sort.by(sortOrder.equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }
}