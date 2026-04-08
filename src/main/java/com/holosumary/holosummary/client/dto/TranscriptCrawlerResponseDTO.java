package com.holosumary.holosummary.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class TranscriptCrawlerResponseDTO {
    private List<Snippet> snippets;

    @JsonAlias("video_id")
    private String videoId;

    @JsonAlias("language_code")
    private String languageCode;

    private String language;

    @JsonAlias("is_generated")
    private boolean isGenerated;

    @Data
    public static class Snippet {
        private String text;
        private Double start;
        private Double end;
    }
}
