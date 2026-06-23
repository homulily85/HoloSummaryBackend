package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.service.SummaryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class SummaryController {
    private final SummaryService summaryService;

    @GetMapping("/summary/{id}")
    public ResponseEntity<?> getSummary(@PathVariable("id") @NotBlank @Size(max = 11, min = 11) String videoId) {
        var summaryOpt = summaryService.getSummary(videoId);

        if (summaryOpt.isPresent()) {
            return ResponseEntity.ok().body(summaryOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("message", "Summary is currently being generated. Please check back later.",
                            "status", "processing"));
        }
    }
}