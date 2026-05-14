package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.service.VideoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
@Validated
public class ScheduleController {

    private final VideoService videoService;

    @GetMapping("/schedule")
    public ResponseEntity<Page<Video>> getSchedule(
            @RequestParam(value = "page-size", defaultValue = "25")
            @Min(1) @Max(100) int pageSize,
            @RequestParam(value = "page-number", defaultValue = "0")
            @Min(0) int pageNumber,
            @RequestParam(value = "status", required = false)
            @Size(max = 16) String status,
            @RequestParam(value = "available-after", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime availableAfter) {

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
