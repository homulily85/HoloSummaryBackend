package com.holosumary.holosummary.service;

import com.holosumary.holosummary.client.OpenRouterApiClient;
import com.holosumary.holosummary.exception.ExternalServiceException;
import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Summary;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.SummaryRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class SummaryService {
    private final SummaryRepository summaryRepository;
    private final VideoRepository videoRepository;
    private final TranscriptService transcriptService;
    private final OpenRouterApiClient openRouterApiClient;

    private final ConcurrentMap<String, Boolean> inProgress = new ConcurrentHashMap<>();

    public SummaryService(SummaryRepository summaryRepository,
                          VideoRepository videoRepository,
                          TranscriptService transcriptService,
                          OpenRouterApiClient openRouterApiClient) {
        this.summaryRepository = summaryRepository;
        this.videoRepository = videoRepository;
        this.transcriptService = transcriptService;
        this.openRouterApiClient = openRouterApiClient;
    }

    public Optional<Summary> getSummary(String videoId) {
        var video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Video not found: " + videoId));

        var oldSummary = summaryRepository.getSummariesByVideo(video);
        if (oldSummary != null) {
            return Optional.of(oldSummary);
        }

        if (inProgress.putIfAbsent(videoId, true) == null) {
            CompletableFuture.runAsync(() -> generateSummaryBackground(videoId));
        }

        return Optional.empty();
    }

    private void generateSummaryBackground(String videoId) {
        try {
            Video video = videoRepository.findWithDetailsById(videoId).orElse(null);

            if (video == null) {
                log.warn("Video {} no longer exists. Aborting background summary.", videoId);
                return;
            }

            if (summaryRepository.getSummariesByVideo(video) != null) {
                return;
            }

            var transcript = transcriptService.getTranscript(videoId, "ja");
            transcript.setVideo(video);
            var dto = openRouterApiClient.fetchSummary(transcript);

            if (dto == null) {
                throw new ExternalServiceException("Failed to fetch summary from OpenRouter API");
            }

            Summary summary = new Summary();
            summary.setText(dto.getChoices().getFirst().getMessage().getContent());
            summary.setVideo(video);
            summary.setPromptTokens(dto.getUsage().getPromptTokens());
            summary.setCompletionTokens(dto.getUsage().getCompletionTokens());
            summary.setTotalTokens(dto.getUsage().getTotalTokens());
            summary.setModel(dto.getModel());

            summaryRepository.save(summary);

        } catch (Exception e) {
            log.error("Failed to generate summary for video {}: {}", videoId, e.getMessage(), e);
        } finally {
            inProgress.remove(videoId);
        }
    }
}