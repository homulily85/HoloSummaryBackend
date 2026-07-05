package com.holosumary.holosummary.service;

import com.holosumary.holosummary.client.TranscriptCrawlerApiClient;
import com.holosumary.holosummary.dto.transcript.TranscriptCrawlerResponseDTO;
import com.holosumary.holosummary.exception.ExternalServiceException;
import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Transcript;
import com.holosumary.holosummary.repository.TranscriptsRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TranscriptService {

    private static final String TRANSCRIPT_RETRY_MESSAGE =
            "Something went wrong while generating transcript, try again later.";

    private final TranscriptCrawlerApiClient apiClient;
    private final TranscriptsRepository transcriptsRepository;
    private final VideoRepository videoRepository;

    public Transcript getTranscript(String videoId, String languageCode) {
        var video = videoRepository.findByVideoId(videoId)
                .orElseThrow(() -> new NotFoundException("Video not found: " + videoId));

        Transcript oldTranscript = transcriptsRepository.findByVideoAndLanguageCode(video, languageCode);

        if (oldTranscript != null) {
            return oldTranscript;
        }

        TranscriptCrawlerResponseDTO dto = apiClient.fetchTranscript(videoId, languageCode);
        if (dto == null) {
            throw new ExternalServiceException(TRANSCRIPT_RETRY_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Transcript transcript = new Transcript();
        transcript.setText(dto.getText());
        transcript.setLanguageCode(dto.getLanguageCode());
        transcript.setLanguage(dto.getLanguage());
        transcript.setGenerated(dto.isGenerated());
        transcript.setVideo(video);

        return transcriptsRepository.save(transcript);
    }
}