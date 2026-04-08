package com.holosumary.holosummary.client;

import com.holosumary.holosummary.client.dto.TranscriptCrawlerRequestDTO;
import com.holosumary.holosummary.client.dto.TranscriptCrawlerResponseDTO;
import com.holosumary.holosummary.model.Transcript;
import com.holosumary.holosummary.model.TranscriptSnippet;
import com.holosumary.holosummary.model.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TranscriptCrawlerApiClient {

    private final RestClient restClient;

    @Value("${name.transcription-api-client.url}")
    private String url;

    public TranscriptCrawlerApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Transcript fetchTranscript(String videoId, String languageCode) {
        TranscriptCrawlerResponseDTO responseDto = restClient
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(new TranscriptCrawlerRequestDTO(videoId, languageCode))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (responseDto == null) {
            return null;
        }

        return mapToEntity(responseDto);
    }

    private Transcript mapToEntity(TranscriptCrawlerResponseDTO dto) {
        Transcript transcript = new Transcript();
        transcript.setLanguage(dto.getLanguage());
        transcript.setLanguageCode(dto.getLanguageCode());
        transcript.setGenerated(dto.isGenerated());

        Video videoRef = new Video();
        videoRef.setId(dto.getVideoId());
        transcript.setVideo(videoRef);

        if (dto.getSnippets() != null) {
            for (TranscriptCrawlerResponseDTO.Snippet snippetDto : dto.getSnippets()) {
                TranscriptSnippet snippet = new TranscriptSnippet();
                snippet.setText(snippetDto.getText());
                snippet.setStart(snippetDto.getStart());
                snippet.setEnd(snippetDto.getEnd());

                transcript.addSnippet(snippet);
            }
        }

        return transcript;
    }
}