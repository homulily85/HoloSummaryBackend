package com.holosumary.holosummary.service;

import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class VideoService {
    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Page<Video> getVideosByStatus(String status, int pageNumber, int pageSize) {
        return videoRepository.findByStatus(status, PageRequest.of(pageNumber, pageSize));
    }

    public Page<Video> getAllVideos(int pageNumber, int pageSize) {
        return videoRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public Page<Video> getVideosAvailableAfter(OffsetDateTime availableAfter, int pageNumber,
                                               int pageSize) {
        return videoRepository.findByAvailableAtAfter(availableAfter,
                PageRequest.of(pageNumber, pageSize));
    }

    public Page<Video> getVideoByStatusAndAvailableAfter(String status,
                                                         OffsetDateTime availableAfter,
                                                         int pageNumber,
                                                         int pageSize) {
        return videoRepository.findByStatusAndAvailableAtAfter(status, availableAfter,
                PageRequest.of(pageNumber, pageSize));
    }

}
