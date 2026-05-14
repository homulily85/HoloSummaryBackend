package com.holosumary.holosummary.dto.openrouter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OpenRouterRequestDTO {
    private String model;
    private List<MessageDTO> messages;
    private List<ToolDTO> tools;
}
