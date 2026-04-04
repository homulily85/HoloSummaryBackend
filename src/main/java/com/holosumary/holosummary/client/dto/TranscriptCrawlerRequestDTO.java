package com.holosumary.holosummary.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranscriptCrawlerRequestDTO {
    @JsonProperty("video_id")
    private String videoId;

    @JsonProperty("language_code")
    private String languageCode;

}
