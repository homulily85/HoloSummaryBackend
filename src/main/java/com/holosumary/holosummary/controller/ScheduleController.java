package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.service.VideoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
@Validated
public class ScheduleController {

    private final VideoService videoService;

    @GetMapping("/schedule")
    public ResponseEntity<Page<Video>> getSchedule(
            @RequestParam(value = "pageSize", defaultValue = "25")
            @Min(1) @Max(100) int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0")
            @Min(0) int pageNumber,
            @RequestParam(value = "status", required = false)
            @Size(max = 16) String status,
            @RequestParam(value = "availableAfter", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime availableAfter,
            @RequestParam(value = "sortBy", defaultValue = "availableAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {

        int mask = (status != null ? 1 : 0) | (availableAfter != null ? 2 : 0);
        Page<Video> videos = switch (mask) {
            case 0 -> videoService.getAllVideos(pageNumber, pageSize, sortBy, sortOrder);
            case 1 ->
                    videoService.getVideosByStatus(status, pageNumber, pageSize, sortBy, sortOrder);
            case 2 ->
                    videoService.getVideosAvailableAfter(availableAfter, pageNumber, pageSize,
                            sortBy, sortOrder);
            default -> videoService.getVideoByStatusAndAvailableAfter(status, availableAfter,
                    pageNumber, pageSize, sortBy, sortOrder);
        };

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable String id) {
        Video video = videoService.getVideoById(id);
        return ResponseEntity.ok(video);
    }
}
