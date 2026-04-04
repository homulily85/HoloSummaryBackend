package com.holosumary.holosummary.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class TranscriptCrawlerResponseDTO {
    private String text;

    @JsonAlias("video_id")
    private String videoId;

    @JsonAlias("language_code")
    private String languageCode;

    private String language;

    @JsonAlias("is_generated")
    private boolean isGenerated;
}
