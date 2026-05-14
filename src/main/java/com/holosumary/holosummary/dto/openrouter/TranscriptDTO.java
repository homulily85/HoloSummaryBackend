package com.holosumary.holosummary.dto.openrouter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TranscriptDTO {
    private String text;
    private List<String> speakerName;
}
