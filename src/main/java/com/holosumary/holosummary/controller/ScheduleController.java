package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.service.VideoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@RestController()
public class ScheduleController {

    private final VideoService videoService;

    public ScheduleController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/schedule")
    public ResponseEntity<Page<Video>> getSchedule(@RequestParam(value = "page-size",
                                                           defaultValue = "25") int pageSize,
                                                   @RequestParam(value = "page-number",
                                                           defaultValue = "0") int pageNumber,
                                                   @RequestParam(value = "status", required =
                                                           false) String status,
                                                   @RequestParam(value = "available-after",
                                                           required = false) OffsetDateTime availableAfter) {
        Page<Video> videos = null;

        if (status == null && availableAfter == null) {
            videos = videoService.getAllVideos(pageNumber, pageSize);
        }

        if (status != null && availableAfter == null) {
            videos = videoService.getVideosByStatus(status, pageNumber, pageSize);
        }

        if (status == null && availableAfter != null) {
            videos = videoService.getVideosAvailableAtAfter(availableAfter, pageNumber, pageSize);
        }

        if (videos == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not use both 'status' " +
                    "and 'available-after' at the same time ");
        } else {
            return ResponseEntity.ok().body(videos);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<List<Video>> getVideosFromHolodex() {
        return ResponseEntity.ok().body(videoService.getVideosFromHolodex());
    }

}
