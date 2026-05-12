package com.holosumary.holosummary.service;

import com.holosumary.holosummary.client.TranscriptCrawlerApiClient;
import com.holosumary.holosummary.client.dto.TranscriptCrawlerResponseDTO;
import com.holosumary.holosummary.exception.NotFoundException;
import com.holosumary.holosummary.model.Transcript;
import com.holosumary.holosummary.repository.TranscriptsRepository;
import com.holosumary.holosummary.repository.VideoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TranscriptService {
    private final TranscriptCrawlerApiClient apiClient;
    private final TranscriptsRepository transcriptsRepository;
    private final VideoRepository videoRepository;

    public Transcript getTranscript(String videoId, String languageCode) {
        var video = videoRepository.findById(videoId);
        if (video.isEmpty()) {
            throw new NotFoundException("");
        }

        List<Transcript> oldTranscript =
                transcriptsRepository.findByVideoAndLanguageCode(video.get(),
                        languageCode);

        if (oldTranscript.isEmpty()) {
            TranscriptCrawlerResponseDTO dto = apiClient.fetchTranscript(videoId, languageCode);

            if (dto == null) {
                throw new NotFoundException("");
            }

            Transcript transcript = new Transcript();
            transcript.setText(dto.getText());
            transcript.setLanguageCode(dto.getLanguageCode());
            transcript.setLanguage(dto.getLanguage());
            transcript.setGenerated(dto.isGenerated());

            transcript.setVideo(video.get());
            return transcriptsRepository.save(transcript);
        } else return oldTranscript.get(0);

    }
}