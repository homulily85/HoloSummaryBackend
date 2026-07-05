package com.holosumary.holosummary.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.holosumary.holosummary.client.OpenRouterApiClient;
import com.holosumary.holosummary.exception.ExternalServiceException;
import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Summary;
import com.holosumary.holosummary.model.Video;
import com.holosumary.holosummary.repository.SummaryRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SummaryService {

    private static final String TRANSCRIPT_RETRY_MESSAGE =
            "Something went wrong while generating transcript, try again later.";
    private static final String CANNOT_GENERATE_SUMMARY_MESSAGE =
            "Cannot generate summary for this video. It may be because the video " +
                    "is a membership video, is no longer available, " +
                    "or transcripts are disabled.";

    private final SummaryRepository summaryRepository;
    private final VideoRepository videoRepository;
    private final TranscriptService transcriptService;
    private final OpenRouterApiClient openRouterApiClient;

    private final ConcurrentMap<String, Boolean> inProgress = new ConcurrentHashMap<>();

    private final Cache<String, ExternalServiceException> failedSummaries = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

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
        var video = videoRepository.findByVideoId(videoId)
                .orElseThrow(() -> new NotFoundException("Video not found: " + videoId));

        var oldSummary = summaryRepository.getSummariesByVideo(video);
        if (oldSummary != null) {
            failedSummaries.invalidate(videoId);
            return Optional.of(oldSummary);
        }

        var failedSummary = failedSummaries.getIfPresent(videoId);
        if (failedSummary != null) {
            throw failedSummary;
        }

        if (inProgress.putIfAbsent(videoId, true) == null) {
            CompletableFuture.runAsync(() -> generateSummaryBackground(videoId));
        }

        return Optional.empty();
    }

    private void generateSummaryBackground(String videoId) {
        try {
            Video video = videoRepository.findWithDetailsByVideoId(videoId).orElse(null);
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
            failedSummaries.invalidate(videoId);

        } catch (NotFoundException e) {
            failedSummaries.put(videoId, new ExternalServiceException(CANNOT_GENERATE_SUMMARY_MESSAGE, HttpStatus.UNPROCESSABLE_CONTENT));
        } catch (ExternalServiceException e) {
            if (e.getStatus().is4xxClientError()) {
                failedSummaries.put(videoId, new ExternalServiceException(CANNOT_GENERATE_SUMMARY_MESSAGE, HttpStatus.UNPROCESSABLE_CONTENT));
            } else {
                failedSummaries.put(videoId, createRetryException());
                log.warn("Failed to generate summary for video {}: {}", videoId, e.getMessage());
            }
        } catch (Exception e) {
            failedSummaries.put(videoId, createRetryException());
            log.error("Failed to generate summary for video {}: {}", videoId, e.getMessage(), e);
        } finally {
            inProgress.remove(videoId);
        }
    }

    private ExternalServiceException createRetryException() {
        return new ExternalServiceException(TRANSCRIPT_RETRY_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}