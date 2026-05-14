package com.holosumary.holosummary.service;

import com.holosumary.holosummary.client.OpenRouterApiClient;
import com.holosumary.holosummary.exception.ExternalServiceException;
import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Summary;
import com.holosumary.holosummary.repository.SummaryRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SummaryService {
    private final SummaryRepository summaryRepository;
    private final VideoRepository videoRepository;
    private final TranscriptService transcriptService;
    private final OpenRouterApiClient openRouterApiClient;

    public Summary getSummary(String videoId) {
        var video = videoRepository.findById(videoId);
        if (video.isEmpty()) {
            throw new NotFoundException("Video not found: " + videoId);
        }

        var oldSummary = summaryRepository.getSummariesByVideo(video.get());

        if (oldSummary == null) {
            var transcript = transcriptService.getTranscript(videoId, "ja");

            var dto = openRouterApiClient.fetchSummary(transcript);

            if (dto == null) {
                throw new ExternalServiceException("Failed to fetch summary from OpenRouter API");
            }

            Summary summary = new Summary();
            summary.setText(dto.getChoices().getFirst().getMessage().getContent());
            summary.setVideo(video.get());
            summary.setPromptTokens(dto.getUsage().getPromptTokens());
            summary.setCompletionTokens(dto.getUsage().getCompletionTokens());
            summary.setTotalTokens(dto.getUsage().getTotalTokens());
            summary.setModel(dto.getModel());

            return summaryRepository.save(summary);
        }
        return oldSummary;
    }
}
