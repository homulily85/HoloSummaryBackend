package com.holosumary.holosummary.service;

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

    public Page<Video> getVideosByStatus(String status, int pageNumber, int pageSize,
                                         String sortBy, String sortOrder) {
        return videoRepository.findByStatus(status, PageRequest.of(pageNumber, pageSize,
                Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC :
                        Sort.Direction.DESC, sortBy)));
    }

    public Page<Video> getAllVideos(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        return videoRepository.findAll(PageRequest.of(pageNumber, pageSize,
                Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC :
                        Sort.Direction.DESC, sortBy)));
    }

    public Page<Video> getVideosAvailableAfter(OffsetDateTime availableAfter, int pageNumber,
                                               int pageSize, String sortBy, String sortOrder) {
        return videoRepository.findByAvailableAtAfter(availableAfter, PageRequest.of(pageNumber,
                pageSize, Sort.by(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC :
                        Sort.Direction.DESC, sortBy)));
    }

    public Page<Video> getVideoByStatusAndAvailableAfter(String status,
                                                         OffsetDateTime availableAfter,
                                                         int pageNumber, int pageSize,
                                                         String sortBy, String sortOrder) {
        return videoRepository.findByStatusAndAvailableAtAfter(status, availableAfter,
                PageRequest.of(pageNumber, pageSize, Sort.by(sortOrder.equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, sortBy)));
    }

}
