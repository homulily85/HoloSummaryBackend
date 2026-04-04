package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.service.VideoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController()
@AllArgsConstructor
public class ScheduleController {

    private final VideoService videoService;

    @GetMapping("/schedule")
    public ResponseEntity<Page<Video>> getSchedule(
            @RequestParam(value = "page-size", defaultValue = "25") int pageSize,
            @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "available-after", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime availableAfter) {

        pageSize = Math.max(1, Math.min(pageSize, 100));
        pageNumber = Math.max(0, pageNumber);

        int mask = (status != null ? 1 : 0) | (availableAfter != null ? 2 : 0);
        Page<Video> videos = switch (mask) {
            case 0 -> videoService.getAllVideos(pageNumber, pageSize);
            case 1 -> videoService.getVideosByStatus(status, pageNumber, pageSize);
            case 2 -> videoService.getVideosAvailableAfter(availableAfter, pageNumber, pageSize);
            default ->
                    videoService.getVideoByStatusAndAvailableAfter(status, availableAfter,
                            pageNumber, pageSize);
        };

        return ResponseEntity.ok(videos);
    }
}
