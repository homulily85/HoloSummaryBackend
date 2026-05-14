package com.holosumary.holosummary.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.holosumary.holosummary.dto.transcript.TranscriptCrawlerRequestDTO;
import com.holosumary.holosummary.dto.transcript.TranscriptCrawlerResponseDTO;
import com.holosumary.holosummary.exception.ExternalServiceException;
import com.holosumary.holosummary.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class TranscriptCrawlerApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${transcription-api-client.url}")
    private String url;

    public TranscriptCrawlerApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TranscriptCrawlerResponseDTO fetchTranscript(String videoId, String languageCode) {
        try {
            return restClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(new TranscriptCrawlerRequestDTO(videoId, languageCode))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (HttpStatusCodeException ex) {
            String responseBody = ex.getResponseBodyAsString();
            String detail = extractDetail(responseBody, ex.getStatusText());
            HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
            if (status == HttpStatus.NOT_FOUND) {
                throw new NotFoundException(detail);
            }
            if (status == HttpStatus.FORBIDDEN) {
                throw new ExternalServiceException(detail, HttpStatus.FORBIDDEN, responseBody);
            }
            throw new ExternalServiceException(detail, HttpStatus.BAD_GATEWAY, responseBody);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Transcript service unavailable",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private String extractDetail(String body, String fallback) {
        if (body == null || body.isBlank()) {
            return fallback == null || fallback.isBlank() ? "Transcript service error" : fallback;
        }
        try {
            JsonNode node = objectMapper.readTree(body);
            JsonNode detail = node.get("detail");
            if (detail != null && detail.isTextual()) {
                return detail.asText();
            }
        } catch (Exception ignored) {
        }
        return body;
    }
}