package com.holosumary.holosummary.service;

import com.holosumary.holosummary.client.HolodexVideosApiClient;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final HolodexVideosApiClient holodexVideosApiClient;

    public VideoService(VideoRepository videoRepository,
                        HolodexVideosApiClient holodexVideosApiClient) {
        this.videoRepository = videoRepository;
        this.holodexVideosApiClient = holodexVideosApiClient;
    }

    public Page<Video> getVideosByStatus(String status, int pageNumber, int pageSize) {
        return videoRepository.findByStatus(status, PageRequest.of(pageNumber, pageSize));
    }

    public Page<Video> getAllVideos(int pageNumber, int pageSize) {
        return videoRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public Page<Video> getVideosAvailableAtAfter(OffsetDateTime offsetDateTime, int pageNumber,
                                                 int pageSize) {
        return videoRepository.findByAvailableAtAfter(offsetDateTime, PageRequest.of(pageNumber,
                pageSize));
    }

    public List<Video> getVideosFromHolodex() {
        return holodexVideosApiClient.fetchRecentVideos();
    }

}
