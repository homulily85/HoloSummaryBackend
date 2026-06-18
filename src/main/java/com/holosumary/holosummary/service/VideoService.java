package com.holosumary.holosummary.service;

import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;

    public Page<Video> getVideosByStatus(String status,
                                         int pageNumber,
                                         int pageSize,
                                         String sortBy,
                                         String sortOrder) {
        return videoRepository.findByStatus(status, getPageRequest(pageNumber
                , pageSize, sortBy, sortOrder));
    }

    public Page<Video> getAllVideos(int pageNumber, int pageSize,
                                    String sortBy,
                                    String sortOrder) {
        return videoRepository.findAll(getPageRequest(pageNumber, pageSize,
                sortBy, sortOrder));
    }

    public Page<Video> getVideosAvailableAfter(OffsetDateTime availableAfter,
                                               int pageNumber,
                                               int pageSize,
                                               String sortBy,
                                               String sortOrder) {
        return videoRepository.findByAvailableAtAfter(availableAfter,
                getPageRequest(pageNumber, pageSize, sortBy, sortOrder));
    }

    public Page<Video> getVideoByStatusAndAvailableAfter(String status,
                                                         OffsetDateTime availableAfter,
                                                         int pageNumber,
                                                         int pageSize,
                                                         String sortBy,
                                                         String sortOrder) {
        return videoRepository.findByStatusAndAvailableAtAfter(status,
                availableAfter, getPageRequest(pageNumber, pageSize,
                        sortBy, sortOrder));
    }

    public Video getVideoById(String id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Video not found " +
                        "with id: " + id));
    }

    private PageRequest getPageRequest(int pageNumber, int pageSize,
                                       String sortBy, String sortOrder) {
        return PageRequest.of(pageNumber, pageSize,
                Sort.by(sortOrder.equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }
}