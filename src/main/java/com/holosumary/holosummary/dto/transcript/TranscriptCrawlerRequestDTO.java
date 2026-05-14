package com.holosumary.holosummary.dto.transcript;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranscriptCrawlerRequestDTO {
    private String videoId;

    private String languageCode;

}
