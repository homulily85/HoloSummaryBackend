package com.holosumary.holosummary.dto;

import lombok.Data;

@Data
public class TranscriptCrawlerResponseDTO {
    private String text;

    private String videoId;

    private String languageCode;

    private String language;

    private boolean isGenerated;
}