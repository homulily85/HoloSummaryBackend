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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateParam,
            @RequestParam(value = "timezone", defaultValue = "UTC") String timezoneStr,
            @RequestParam(value = "sortBy", defaultValue = "availableAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {

        OffsetDateTime date = null;
        if (dateParam != null) {
            try {
                ZoneId zoneId = ZoneId.of(timezoneStr);
                date = dateParam.atStartOfDay(zoneId).toOffsetDateTime();
            } catch (Exception e) {
                date = dateParam.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
            }
        }

        int mask = (status != null ? 1 : 0) | (date != null ? 2 : 0);
        Page<Video> videos = switch (mask) {
            case 0 -> videoService.getAllVideos(pageNumber, pageSize,
                    sortBy, sortOrder);
            case 1 -> videoService.getVideosByStatus(status, pageNumber,
                    pageSize, sortBy, sortOrder);
            case 2 -> videoService.getVideosAvailableIn(date, pageNumber,
                    pageSize, sortBy, sortOrder);
            default -> videoService.getVideoByStatusAndAvailableIn(status, date,
                    pageNumber, pageSize, sortBy, sortOrder);
        };

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable String id) {
        var video = videoService.getVideoByVideoId(id);
        return ResponseEntity.ok(video);
    }
}
