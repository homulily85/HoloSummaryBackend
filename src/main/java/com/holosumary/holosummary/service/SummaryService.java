package com.holosumary.holosummary.service;

import com.holosumary.holosummary.client.OpenRouterApiClient;
import com.holosumary.holosummary.exception.ExternalServiceException;
import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Summary;
import com.holosumary.holosummary.repository.SummaryRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@AllArgsConstructor
public class SummaryService {
    private final SummaryRepository summaryRepository;
    private final VideoRepository videoRepository;
    private final TranscriptService transcriptService;
    private final OpenRouterApiClient openRouterApiClient;

    // Store locks per videoId
    private final ConcurrentMap<String, Lock> locks = new ConcurrentHashMap<>();

    public Summary getSummary(String videoId) {
        var video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Video not found: " + videoId));

        // 1st Check (Fast path without locking)
        var oldSummary = summaryRepository.getSummariesByVideo(video);
        if (oldSummary != null) {
            return oldSummary;
        }

        // Acquire lock for this specific videoId
        Lock lock = locks.computeIfAbsent(videoId, k -> new ReentrantLock());
        lock.lock();
        try {
            // 2nd Check (Inside lock: in case another thread just finished generating it)
            oldSummary = summaryRepository.getSummariesByVideo(video);
            if (oldSummary != null) {
                return oldSummary;
            }

            // Generate summary via OpenRouter API
            var transcript = transcriptService.getTranscript(videoId, "ja");
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

            return summaryRepository.save(summary);
        } finally {
            lock.unlock();
        }
    }
}