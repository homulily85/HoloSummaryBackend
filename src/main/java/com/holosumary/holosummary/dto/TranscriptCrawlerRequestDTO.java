package com.holosumary.holosummary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranscriptCrawlerRequestDTO {
    private String videoId;

    private String languageCode;

}
