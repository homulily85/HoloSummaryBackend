package com.holosumary.holosummary.dto.openrouter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDTO{
    private String role;
    private String content;
}

