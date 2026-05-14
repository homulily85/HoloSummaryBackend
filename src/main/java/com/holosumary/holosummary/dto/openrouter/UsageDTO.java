package com.holosumary.holosummary.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsageDTO{
    @JsonAlias("prompt_tokens")
    private Integer promptTokens;
    @JsonAlias("completion_tokens")
    private Integer completionTokens;
    @JsonAlias("total_tokens")
    private Integer totalTokens;
}