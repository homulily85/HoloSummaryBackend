package com.holosumary.holosummary.client;

import com.holosumary.holosummary.client.dto.TranscriptCrawlerRequestDTO;
import com.holosumary.holosummary.client.dto.TranscriptCrawlerResponseDTO;
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

    public TranscriptCrawlerResponseDTO fetchTranscript(String videoId, String languageCode) {
        return restClient
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(new TranscriptCrawlerRequestDTO(videoId, languageCode))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}