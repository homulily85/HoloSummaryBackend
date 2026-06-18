package com.holosumary.holosummary.service;

import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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

    public Page<Video> getVideosAvailableIn(OffsetDateTime date,
                                            int pageNumber,
                                            int pageSize,
                                            String sortBy,
                                            String sortOrder) {

        OffsetDateTime startOfDay = date.with(LocalTime.MIN);
        OffsetDateTime endOfDay = date.with(LocalTime.MAX);

        return videoRepository.findVideoByAvailableAtIsBetween(
                startOfDay,
                endOfDay,
                getPageRequest(pageNumber, pageSize, sortBy, sortOrder)
        );
    }

    public Page<Video> getVideoByStatusAndAvailableIn(String status,
                                                      OffsetDateTime date,
                                                      int pageNumber,
                                                      int pageSize,
                                                      String sortBy,
                                                      String sortOrder) {
        OffsetDateTime startOfDay = date.with(LocalTime.MIN);
        OffsetDateTime endOfDay = date.with(LocalTime.MAX);

        return videoRepository.findByStatusAndAvailableAtBetween(status,
                startOfDay, endOfDay, getPageRequest(pageNumber, pageSize,
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