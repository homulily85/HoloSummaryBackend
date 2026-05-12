package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Transcript;
import com.holosumary.holosummary.service.TranscriptService;
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
public class TranscriptController {
    private final TranscriptService transcriptService;

    @PostMapping("/transcript")
    public ResponseEntity<Transcript> getTranscript(@RequestBody RequestDTO request) {
        var transcript = transcriptService.getTranscript(request.getVideoId(),
                request.getLanguageCode());

        return ResponseEntity.ok().body(transcript);
    }

    @Data
    public static class RequestDTO {
        private String videoId;

        private String languageCode;
    }

}
