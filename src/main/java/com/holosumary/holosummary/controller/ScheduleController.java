package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.dto.video.VideoResponseDTO;
import com.holosumary.holosummary.service.VideoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
@Validated
public class ScheduleController {

    private final VideoService videoService;

    @GetMapping("/schedule")
    public ResponseEntity<Page<VideoResponseDTO>> getSchedule(
            Authentication authentication,
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
        Integer userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userId =
                    Integer.valueOf(Objects.requireNonNull(authentication.getPrincipal()).toString());
        }

        int mask = (status != null ? 1 : 0) | (availableAfter != null ? 2 : 0);
        Page<VideoResponseDTO> videos = switch (mask) {
            case 0 -> videoService.getAllVideos(pageNumber, pageSize, sortBy,
                    sortOrder, userId);
            case 1 -> videoService.getVideosByStatus(status, pageNumber,
                    pageSize, sortBy, sortOrder, userId);
            case 2 -> videoService.getVideosAvailableAfter(availableAfter,
                    pageNumber, pageSize,
                    sortBy, sortOrder, userId);
            default -> videoService.getVideoByStatusAndAvailableAfter(status,
                    availableAfter,
                    pageNumber, pageSize, sortBy, sortOrder, userId);
        };

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<VideoResponseDTO> getVideo(@PathVariable String id,
                                                     Authentication authentication) {
        Integer userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userId =
                    Integer.valueOf(Objects.requireNonNull(authentication.getPrincipal()).toString());
        }

        var video = videoService.getVideoById(id, userId);
        return ResponseEntity.ok(video);
    }
}
