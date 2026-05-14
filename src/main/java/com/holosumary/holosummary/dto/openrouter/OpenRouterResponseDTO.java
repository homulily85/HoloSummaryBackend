package com.holosumary.holosummary.dto.openrouter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OpenRouterResponseDTO {
    private String model;
    private List<ChoiceDTO> choices;
    private UsageDTO usage;
}

