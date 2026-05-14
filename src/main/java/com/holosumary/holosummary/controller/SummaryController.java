package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Summary;
import com.holosumary.holosummary.service.SummaryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class SummaryController {
    private final SummaryService summaryService;

    @PostMapping("/summary")
    public ResponseEntity<Summary> getSummary(@RequestBody RequestDTO request) {
        var summary = summaryService.getSummary(request.getVideoId());
        return ResponseEntity.ok().body(summary);
    }

    @Data
    public static class RequestDTO {
        private String videoId;
    }
}
