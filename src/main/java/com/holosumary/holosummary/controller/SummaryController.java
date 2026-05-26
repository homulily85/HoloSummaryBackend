package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Summary;
import com.holosumary.holosummary.service.SummaryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class SummaryController {
    private final SummaryService summaryService;

    @GetMapping("/summary/{id}")
    public ResponseEntity<Summary> getSummary(@PathVariable("id") @NotBlank @Size(max = 11, min =
            11) String videoId) {
        var summary = summaryService.getSummary(videoId);
        return ResponseEntity.ok().body(summary);
    }
}
